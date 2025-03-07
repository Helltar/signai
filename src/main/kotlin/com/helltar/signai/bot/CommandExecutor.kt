package com.helltar.signai.bot

import com.helltar.signai.Strings
import com.helltar.signai.commands.BotCommand
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CommandExecutor(private val scope: CoroutineScope) {

    private val requestsMap = hashMapOf<String, Job>()

    private companion object {
        val log = KotlinLogging.logger {}
    }

    fun execute(botCommand: BotCommand) {
        val key = botCommand.envelope.source

        if (!launch(key) { botCommand.run() })
            scope.launch { botCommand.replyToMessage(Strings.MANY_REQUEST) }
    }

    private fun launch(key: String, block: suspend () -> Unit): Boolean {
        if (requestsMap.containsKey(key))
            if (requestsMap[key]?.isCompleted == false)
                return false

        log.debug { "launch --> $key" }

        requestsMap[key] = scope.launch { block() }

        return true
    }
}
