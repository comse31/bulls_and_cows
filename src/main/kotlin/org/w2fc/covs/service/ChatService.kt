package org.w2fc.covs.service

import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.w2fc.covs.model.Status
import org.w2fc.covs.model.UserEntity
import org.w2fc.covs.repository.UserRepository
import org.w2fc.covs.repository.WordRepository
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*
import kotlin.random.Random


@Transactional(propagation = Propagation.REQUIRES_NEW)
@Service
class ChatService(
    val userRepository: UserRepository,
    val wordRepository: WordRepository,
    val client: OkHttpClient,
    @Value("\${telegram.bot.token}")
    token: String
) : TelegramLongPollingBot(token) {

    fun performMessage(body: Update): String {
        val user = getOrCreateUser(body.message.from)
        with(body.message.text) {
            return when (this) {
                HELP_PHRASE -> getRules()
                START_PHRASE -> startGame(user)
                STOP_PHRASE -> stopGame(user)
                else -> checkAnswer(this, user)
            }
        }
    }

    override fun onUpdateReceived(update: Update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        sendMessage(chatId , performMessage(update))
    }

    private fun getOrCreateUser(from: User?): UserEntity {
        return from?.userName?.let { userRepository.findByUserName(it) } ?: userRepository.save(
            UserEntity(
                id = UUID.randomUUID(),
                userName = from?.userName!!,
                status = Status.STOP,
                attempts = 0,
                attemptsMax = 0,
                attemptsMin = 0,
                games = 0,
                level = 1,
                word = ""
            )
        )
    }

    private fun stopGame(user: UserEntity): String {
        if (user.status == Status.STOP) {
            return "Игра не идет"
        }
        user.status = Status.STOP
        return "Игра остановлена на ${user.attempts} попытке"
    }

    private fun startGame(user: UserEntity): String {
        return "Какой уровень 1-3(самый сложный)"
    }

    private fun checkAnswer(body: String, user: UserEntity): String {
        if (isDigit.matches(body)) {
            if (user.status == Status.PROCESS) {
                return "Вы уже в игре, уровень ${user.level}"
            }
            //set level
            user.level = max(min(body.toInt(), 3), 1)
            user.attempts = 0
            user.word = getRandowWord(4 + user.level)
            user.status = Status.PROCESS
            return "Установлен уровень ${user.level}"
        } else if (body == user.word) {
            user.status = Status.STOP
            if (user.attempts < user.attemptsMin) {
                user.attemptsMin = user.attempts
            }
            if (user.attempts > user.attemptsMax) {
                user.attemptsMax = user.attempts
            }
            user.games++
            return "Победа"
        } else {
            if (user.status == Status.STOP) {
                return "Игра еще не начата"
            }
            val symbolsCount = 4 + user.level
            if (body.length != symbolsCount || !checkDictionary(body, symbolsCount)) {
                return "Ошибка"
            } else {
                val bulls = mutableMapOf<Char, Int>()
                var bullsCount = 0
                var covsCount = 0
                val covs = mutableMapOf<Char, Int>()
                var stringForCovs = user.word
                for (i in 0 until symbolsCount) {
                    val checkSymbol = body[i]
                    if (checkSymbol == user.word[i]) {
                        bulls[checkSymbol] = i
                        stringForCovs = stringForCovs.replaceRange(i, i + 1, " ")
                        bullsCount++
                    }
                }
                for (i in 0 until symbolsCount) {
                    if (bulls.values.contains(i)) continue
                    val checkSymbol = body[i]
                    val startPosition = covs[checkSymbol] ?: 0
                    if (stringForCovs.indexOf(checkSymbol, startPosition, true) >= 0) {
                        covs[checkSymbol] = user.word.indexOf(checkSymbol, startPosition, true) + 1
                        covsCount++
                    }
                }
                user.attempts++
                return "$bullsCount быка, $covsCount коровы"
            }
        }
    }

    private fun getRandowWord(length: Int): String {
        val wordCount = wordRepository.countByLength(length)
        return wordRepository.getWordByLengthAndLimit(length, Random.nextInt(wordCount))
    }

    private fun checkDictionary(body: String, symbolsCount: Int): Boolean {
        return wordRepository.findByWordAndLength(body, symbolsCount) != null
    }

    private fun getRules(): String {
        return "Как играть"
    }

    private fun sendMessage(chatId: Long, text: String) {
        val chatIdStr = chatId.toString()
        val sendMessage = SendMessage(chatIdStr, text)
        try {
            execute(sendMessage)
        } catch (e: TelegramApiException) {

        }
    }

    companion object {
        const val HELP_PHRASE = "правила"
        const val START_PHRASE = "старт"
        const val STOP_PHRASE = "стоп"
        val isDigit = "\\d".toRegex()
    }

    override fun getBotUsername(): String {
        TODO("Not yet implemented")
    }


}