package org.w2fc.covs.service

import org.springframework.stereotype.Service
import org.w2fc.covs.model.WordEntity
import org.w2fc.covs.repository.WordRepository
import java.util.*

@Service
class AdminService(
    val wordRepository: WordRepository
) {
    fun initWords() {
        val dict = this::class.java.getClassLoader().getResource("slovar1_0.fb2").readText(Charsets.UTF_8)
        for (i in 5..7) {
            val matches = "<strong>([а-я]{$i})</strong>".toRegex().findAll(dict).map { it.groupValues[1].toLowerCase() }.toSet()
            matches.forEach {
                wordRepository.save(
                    WordEntity(
                        id = UUID.randomUUID(),
                        word = it,
                        length = i
                    )
                )
            }
        }
    }
}