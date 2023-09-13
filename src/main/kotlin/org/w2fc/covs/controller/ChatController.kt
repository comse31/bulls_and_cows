package org.w2fc.covs.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.telegram.telegrambots.meta.api.objects.Update
import org.w2fc.covs.service.ChatService

@RestController
@RequestMapping("/update")
class ChatController(
    val chatService: ChatService
) {
    @PostMapping
    fun receive(@RequestBody body: Update): String {
        return chatService.performMessage(body)
    }
}