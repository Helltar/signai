package com.helltar.signai.bot

import com.helltar.signai.commands.BotCommand
import com.helltar.signai.commands.chat.Chat
import com.helltar.signai.commands.chat.ChatCtx
import com.helltar.signai.commands.chat.ChatRm
import com.helltar.signai.signal.model.Receive

class CommandRegistry {

    private val commandHandlers = mutableMapOf<String, (Receive.Envelope) -> BotCommand>()

    init {
        registerCommands()
    }

    private fun registerCommands() {
        commandHandlers["chat"] = { envelope: Receive.Envelope -> Chat(envelope) }
        commandHandlers["chatrm"] = { envelope: Receive.Envelope -> ChatRm(envelope) }
        commandHandlers["chatctx"] = { envelope: Receive.Envelope -> ChatCtx(envelope) }
    }

    fun getHandler(command: String) =
        commandHandlers[command]
}