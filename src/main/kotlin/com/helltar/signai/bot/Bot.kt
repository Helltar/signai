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
        const val COMMAND_CHAT_RM = "chatrm"
        const val MAX_USER_DIALOG_LENGTH_SUM = 15000
    }

    private val chatGPT = ChatGPT(openaiAPIKey)
    private val signal = Signal(signalAPIUrl, signalPhoneNumber, signalGroupID)

    private val chatContextMap = hashMapOf<String, LinkedList<Chat.MessageData>>()

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
                val author = message.envelope.source
                val timestamp = message.envelope.timestamp

                if (it.startsWith(COMMAND_CHAT)) {
                    val text = it.removePrefix(COMMAND_CHAT).trim()
                    processChat(text, author, timestamp)
                } else if (it.trim() == COMMAND_CHAT_RM)
                    removeUserDialogContext(author, timestamp)
            }
        }
    }

    private fun handleUserReplyToBot(messages: List<Receive.ResponseData>) {
        messages.filter { isValidReply(it) }.forEach {
            val text = it.envelope.dataMessage!!.message!!
            val author = it.envelope.source
            val timestamp = it.envelope.timestamp
            processChat(text, author, timestamp)
        }
    }

    private fun removeUserDialogContext(replyAuthor: String, replyId: Long) {
        chatContextMap.remove(replyAuthor)
        signal.replyToMessage("Context has been removed \uD83D\uDC4C", replyAuthor, replyId)
    }

    private fun processChat(text: String, replyAuthor: String, replyId: Long) {
        if (text.isBlank()) return

        try {
            signal.showTypingIndicator()

            if (!chatContextMap.containsKey(replyAuthor)) {
                val chatSystemMessageData = Chat.MessageData(CHAT_ROLE_SYSTEM, chatSystemPrompt)
                chatContextMap[replyAuthor] = LinkedList(listOf(chatSystemMessageData))
            }

            chatContextMap[replyAuthor]?.add(Chat.MessageData(CHAT_ROLE_USER, text))

            var contextLength = getUserDialogLengthSum(replyAuthor)

            if (contextLength > MAX_USER_DIALOG_LENGTH_SUM) {
                while (contextLength > MAX_USER_DIALOG_LENGTH_SUM) {
                    chatContextMap[replyAuthor]?.removeAt(1)
                    contextLength = getUserDialogLengthSum(replyAuthor)
                }
            }

            val answer = chatGPT.sendPrompt(chatContextMap[replyAuthor]!!)

            chatContextMap[replyAuthor]?.add(Chat.MessageData(CHAT_ROLE_ASSISTANT, answer))

            log.debug("{}: {}", replyAuthor, chatContextMap[replyAuthor])

            signal.replyToMessage(answer, replyAuthor, replyId)
        } catch (e: Exception) {
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

    private fun getUserDialogLengthSum(key: String) =
        chatContextMap[key]!!.sumOf { it.content.length }
}