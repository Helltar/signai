package com.helltar.signai.bot

import com.helltar.signai.commands.BotCommand
import com.helltar.signai.commands.ChatDeps
import com.helltar.signai.commands.chat.Chat
import com.helltar.signai.commands.chat.ChatCtx
import com.helltar.signai.commands.chat.ChatRm
import com.helltar.signai.signal.model.Receive

class CommandRegistry(private val chatDeps: ChatDeps) {

    private val commandHandlers: Map<String, (Receive.Envelope) -> BotCommand> =
        mapOf(
            "chat" to { envelope -> Chat(envelope, chatDeps) },
            "chatrm" to { envelope -> ChatRm(envelope, chatDeps) },
            "chatctx" to { envelope -> ChatCtx(envelope, chatDeps) }
        )

    fun getHandler(command: String) =
        commandHandlers[command.lowercase()]
}
