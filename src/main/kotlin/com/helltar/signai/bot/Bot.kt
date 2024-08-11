package com.helltar.signai.bot

import com.helltar.signai.EnvConfig.signalAPIUrl
import com.helltar.signai.EnvConfig.signalPhoneNumber
import com.helltar.signai.commands.CommandExecutor
import com.helltar.signai.commands.chat.Chat
import com.helltar.signai.signal.Signal
import com.helltar.signai.signal.model.Receive
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.io.File

class Bot(username: String, name: String, avatar: File) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val signal = Signal(signalAPIUrl, signalPhoneNumber)

    private val commandRegistry = CommandRegistry()
    private val commandExecutor = CommandExecutor(scope)

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        log.info("set username: ${signal.setUsername(username).data.decodeToString()}")
        log.info("update profile, name: [$name], avatar size: [${avatar.length()} bytes]")
        signal.updateProfile(name, avatar)
        log.info("start ...")
    }

    fun run() = scope.launch {
        while (isActive) {
            try {
                val messages = signal.receive()

                if (messages.isNotEmpty()) {
                    val messagesWithNoReply = messages.filter { it.envelope.dataMessage?.quote == null }
                    val messagesWithReplyToBot = messages.filter { isValidReply(it) }

                    handleCommands(messagesWithNoReply)

                    messagesWithReplyToBot.forEach {
                        commandExecutor.execute(Chat(it.envelope))
                    }
                }

                delay(1000)
            } catch (e: Exception) {
                log.error(e.message)
                delay(5000)
            }
        }
    }

    private fun handleCommands(messages: List<Receive.Response>) {
        messages.forEach { message ->
            message.envelope.dataMessage?.message?.let { text ->
                val command = text.split(" ").first()

                commandRegistry.getHandler(command)?.let {
                    val envelope = message.envelope.apply { this.dataMessage?.message = text.removePrefix(command) }
                    commandExecutor.execute(it(envelope))
                }
            }
        }
    }

    private fun isValidReply(response: Receive.Response): Boolean {
        val dataMessage = response.envelope.dataMessage

        return dataMessage?.quote != null &&
                dataMessage.quote.authorNumber == signalPhoneNumber &&
                dataMessage.message != null
    }
}