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
import kotlin.time.Duration.Companion.seconds

class Bot(private val config: Config.BotConfig) {

    private val signal = Signal(config.signalAPIUrl, config.signalPhoneNumber, KtorClient)
    private val chatGPT = ChatGPT(config.openaiAPIKey, config.gptModel, KtorClient)

    private val chatDeps = ChatDeps(CommandDeps(signal), config.chatSystemPrompt, chatGPT)
    private val commandRegistry = CommandRegistry(chatDeps)

    private val receiveScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val commandScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val commandExecutor = CommandExecutor(commandScope, userRequestsPerHour = config.userRPH)

    private companion object {
        val whitespaceRegex = Regex("\\s+")
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
        log.info { "🚀 start ..." }
    }

    private fun run() = receiveScope.launch {
        while (isActive) {
            try {
                signal.receiveEach { message ->
                    if (message.envelope.dataMessage?.quote == null) {
                        handleCommands(listOf(message))
                    }

                    if (isValidReply(message)) {
                        commandExecutor.execute(Chat(message.envelope, chatDeps))
                    }
                }

                log.info { "websocket connection closed, reconnecting ..." }
                delay(1.seconds)
            } catch (e: CancellationException) {
                log.debug { "bot loop cancelled" }
                throw e
            } catch (e: Exception) {
                log.error(e) { "❌ bot loop failed: ${e::class.simpleName}: ${e.message}" }
                log.info { "⏳ delay 10 seconds before retry ..." }
                delay(10.seconds)
            }
        }
    }

    private fun handleCommands(messages: List<Receive.Response>) {
        messages.forEach { message ->
            message.envelope.dataMessage?.message?.let { text ->
                val parts = text.trim().split(whitespaceRegex, limit = 2)
                val command = parts[0]

                commandRegistry.getHandler(command)?.let {
                    val args = parts.getOrNull(1) ?: ""
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
