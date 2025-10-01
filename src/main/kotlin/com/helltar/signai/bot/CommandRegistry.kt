package com.helltar.signai.bot

import com.helltar.signai.commands.BotCommand
import com.helltar.signai.commands.chat.Chat
import com.helltar.signai.commands.chat.ChatCtx
import com.helltar.signai.commands.chat.ChatRm
import com.helltar.signai.signal.model.Receive

class CommandRegistry {

    private val commandHandlers = mutableMapOf<String, (Receive.Envelope) -> BotCommand>()

    init {
        initializeCommands()
    }

    private fun initializeCommands() {
        registerCommands(
            "chat" to ::Chat,
            "chatrm" to ::ChatRm,
            "chatctx" to ::ChatCtx
        )
    }

    private fun registerCommands(vararg commands: Pair<String, (Receive.Envelope) -> BotCommand>) {
        commands.forEach { (command, handler) ->
            commandHandlers[command] = handler
        }
    }

    fun getHandler(command: String) =
        commandHandlers[command.lowercase()]
}
