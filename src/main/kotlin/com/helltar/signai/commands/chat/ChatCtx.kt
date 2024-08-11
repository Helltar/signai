package com.helltar.signai.commands.chat

import com.helltar.signai.Strings
import com.helltar.signai.gpt.model.Chat.CHAT_ROLE_USER
import com.helltar.signai.signal.model.Receive

class ChatCtx(envelope: Receive.Envelope) : Chat(envelope) {

    override fun run() {
        val text =
            if (userChatDialogHistory.isNotEmpty()) {
                userChatDialogHistory
                    .filter { it.role == CHAT_ROLE_USER }
                    .joinToString("\n") { "- ${it.content}" }
            } else
                Strings.CHAT_CONTEXT_EMPTY

        replyToMessage(text)
    }
}