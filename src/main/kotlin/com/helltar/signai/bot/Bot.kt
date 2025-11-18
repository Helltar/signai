package com.helltar.signai.bot

import com.helltar.signai.Config.chatSystemPrompt
import com.helltar.signai.Config.gptModel
import com.helltar.signai.Config.signalAPIUrl
import com.helltar.signai.Config.signalPhoneNumber
import com.helltar.signai.commands.chat.Chat
import com.helltar.signai.signal.Signal
import com.helltar.signai.signal.model.Receive
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import java.io.File

class Bot(private val username: String, private val name: String, private val avatar: File) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val signal = Signal(signalAPIUrl, signalPhoneNumber)

    private val commandRegistry = CommandRegistry()
    private val commandExecutor = CommandExecutor(scope)

    private var isInitialized = false

    private companion object {
        val log = KotlinLogging.logger {}
    }

    fun run() = scope.launch {
        while (isActive) {
            try {
                if (!isInitialized) {
                    init()
                    isInitialized = true
                }

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
                log.error { e.message }
                log.info { "delay 10 seconds ..." }
                delay(10000)
            }
        }
    }

    private fun init() {
        log.info { "set username: ${signal.setUsername(username).data.decodeToString()}" }
        log.info { "update profile, name: [$name], avatar size: [${avatar.length()} bytes]" }
        log.info { "$signalPhoneNumber, gptModel: $gptModel, systemPrompt: $chatSystemPrompt" }
        signal.updateProfile(name, avatar)
        log.info { "start ..." }
    }

    private fun handleCommands(messages: List<Receive.Response>) {
        messages.forEach { message ->
            message.envelope.dataMessage?.message?.let { text ->
                val command = text.split(" ").first()

                commandRegistry.getHandler(command)?.let {
                    val envelope = message.envelope.apply { this.dataMessage?.message = text.removePrefix(command).trim() }
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
