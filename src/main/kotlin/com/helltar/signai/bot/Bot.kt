package com.helltar.signai.bot

import com.helltar.signai.Config
import com.helltar.signai.commands.ChatDeps
import com.helltar.signai.commands.CommandDeps
import com.helltar.signai.commands.chat.Chat
import com.helltar.signai.gpt.ChatGPT
import com.helltar.signai.network.KtorClient
import com.helltar.signai.signal.Signal
import com.helltar.signai.signal.model.Receive
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*

class Bot(private val config: Config.BotConfig) {

    private val signal = Signal(config.signalAPIUrl, config.signalPhoneNumber, KtorClient)
    private val chatGPT = ChatGPT(config.openaiAPIKey, config.gptModel, KtorClient)

    private val chatDeps = ChatDeps(CommandDeps(signal), config.chatSystemPrompt, chatGPT)

    private val commandRegistry = CommandRegistry(chatDeps)
    private val scope = CoroutineScope(Dispatchers.IO)
    private val commandExecutor = CommandExecutor(scope, userRequestsPerHour = config.userRPH)

    private companion object {
        val log = KotlinLogging.logger {}
    }

    suspend fun start() {
        init()
        run().join()
    }

    private suspend fun init() {
        log.info { "⚙️ bot config: ${config.toSafeLog()}" }
        val usernameResponse = signal.setUsername(config.botUsername)
        log.info { "ℹ\uFE0F set username response: $usernameResponse" }
        signal.updateProfile(config.botName, config.avatar)
        signal.setAccountSettings(trustMode = "always")
        log.info { "🚀 start ..." }
    }

    private fun run() = scope.launch {
        while (isActive) {
            try {
                val messages = signal.receive()

                if (messages.isNotEmpty()) {
                    val messagesWithNoReply = messages.filter { it.envelope.dataMessage?.quote == null }
                    val messagesWithReplyToBot = messages.filter { isValidReply(it) }

                    handleCommands(messagesWithNoReply)

                    messagesWithReplyToBot.forEach {
                        commandExecutor.execute(Chat(it.envelope, chatDeps))
                    }
                }

                delay(1000)
            } catch (e: Exception) {
                log.error(e) { "❌ bot loop failed: ${e::class.simpleName}: ${e.message}" }
                log.info { "⏳ delay 10 seconds before retry ..." }
                delay(10_000)
            }
        }
    }

    private fun handleCommands(messages: List<Receive.Response>) {
        messages.forEach { message ->
            message.envelope.dataMessage?.message?.let { text ->
                val command = text.substringBefore(' ')

                commandRegistry.getHandler(command)?.let {
                    val args = text.substringAfter(' ', "")
                    val envelope = message.envelope.copy(dataMessage = message.envelope.dataMessage.copy(message = args))
                    commandExecutor.execute(it(envelope))
                }
            }
        }
    }

    private fun isValidReply(response: Receive.Response): Boolean {
        val dataMessage = response.envelope.dataMessage

        return dataMessage?.quote != null &&
                dataMessage.quote.authorNumber == config.signalPhoneNumber &&
                dataMessage.message != null
    }
}
