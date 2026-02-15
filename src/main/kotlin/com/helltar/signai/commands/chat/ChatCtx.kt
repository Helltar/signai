package com.helltar.signai.commands.chat

import com.helltar.signai.Strings
import com.helltar.signai.commands.ChatDeps
import com.helltar.signai.gpt.model.Chat.CHAT_ROLE_USER
import com.helltar.signai.signal.model.Receive

class ChatCtx(envelope: Receive.Envelope, deps: ChatDeps) : Chat(envelope, deps) {

    override suspend fun run() {
        replyToMessage(constructUserDialogText())
    }

    private fun constructUserDialogText() =
        if (userDialogHistory.isNotEmpty()) {
            userDialogHistory
                .filter { it.role == CHAT_ROLE_USER }
                .joinToString("\n") { "- ${it.content}" }
        } else
            Strings.CHAT_CONTEXT_EMPTY
}
