package com.helltar.signai.bot

import com.helltar.signai.EnvConfig.chatSystemPrompt
import com.helltar.signai.EnvConfig.openaiAPIKey
import com.helltar.signai.EnvConfig.signalAPIUrl
import com.helltar.signai.EnvConfig.signalGroupID
import com.helltar.signai.EnvConfig.signalPhoneNumber
import com.helltar.signai.gpt.ChatGPT
import com.helltar.signai.gpt.models.Chat
import com.helltar.signai.gpt.models.Chat.CHAT_ROLE_ASSISTANT
import com.helltar.signai.gpt.models.Chat.CHAT_ROLE_SYSTEM
import com.helltar.signai.gpt.models.Chat.CHAT_ROLE_USER
import com.helltar.signai.signal.Signal
import com.helltar.signai.signal.models.Receive
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*
import kotlin.time.Duration.Companion.seconds

class Bot(username: String, name: String, avatar: File, private val receiveDelaySec: Int = 1) {

    private companion object {
        const val COMMAND_CHAT = "chat "
    }

    private val chatGPT = ChatGPT(openaiAPIKey)
    private val signal = Signal(signalAPIUrl, signalPhoneNumber, signalGroupID)

    private val chatSystemMessageData = Chat.MessageData(CHAT_ROLE_SYSTEM, chatSystemPrompt)
    private val chatContext = LinkedList<Chat.MessageData>().apply { add(chatSystemMessageData) }

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        log.info("set username: ${signal.setUsername(username).data.decodeToString()}")
        log.info("update profile, name: [$name], avatar size: [${avatar.length()} bytes]")
        signal.updateProfile(name, avatar)
        log.info("start ...")
    }

    fun run() = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
            val messages = signal.receive()

            handleChatCommand(messages)
            handleUserReplyToBot(messages)

            delay(receiveDelaySec.seconds.inWholeMilliseconds)
        }
    }

    private fun handleChatCommand(messages: List<Receive.ResponseData>) {
        val messagesWithNoReply = messages.filter { it.envelope.dataMessage?.quote == null }

        messagesWithNoReply.forEach { message ->
            message.envelope.dataMessage?.message?.let {
                if (it.startsWith(COMMAND_CHAT)) {
                    val text = it.removePrefix(COMMAND_CHAT).trim()
                    val author = message.envelope.source
                    val timestamp = message.envelope.timestamp
                    replyToMessage(text, author, timestamp)
                }
            }
        }
    }

    private fun handleUserReplyToBot(messages: List<Receive.ResponseData>) {
        messages.filter { isValidReply(it) }.forEach {
            val text = it.envelope.dataMessage!!.message!!
            val author = it.envelope.source
            val timestamp = it.envelope.timestamp
            replyToMessage(text, author, timestamp)
        }
    }

    private fun replyToMessage(text: String, replyAuthor: String, replyId: Long) {
        if (text.isBlank()) return

        try {
            signal.showTypingIndicator()

            chatContext.add(Chat.MessageData(CHAT_ROLE_USER, text))

            if (chatContext.size > 25)
                chatContext.removeAt(1)

            val answer = chatGPT.sendPrompt(chatContext)

            chatContext.add(Chat.MessageData(CHAT_ROLE_ASSISTANT, answer))

            signal.replyToMessage(answer, replyAuthor, replyId)
        } catch (e: Exception) {
            chatContext.removeAll(chatContext.subList(1, chatContext.size).toSet())
            log.error(e.message)
        } finally {
            signal.showTypingIndicator(false)
        }
    }

    private fun isValidReply(responseData: Receive.ResponseData): Boolean {
        val dataMessage = responseData.envelope.dataMessage

        return dataMessage?.quote != null &&
                dataMessage.quote.authorNumber == signalPhoneNumber &&
                dataMessage.message != null
    }
}