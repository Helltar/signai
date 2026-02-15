package com.helltar.signai.commands.chat

import com.helltar.signai.commands.BotCommand
import com.helltar.signai.commands.ChatDeps
import com.helltar.signai.gpt.model.Chat
import com.helltar.signai.gpt.model.Chat.CHAT_ROLE_ASSISTANT
import com.helltar.signai.gpt.model.Chat.CHAT_ROLE_SYSTEM
import com.helltar.signai.gpt.model.Chat.CHAT_ROLE_USER
import com.helltar.signai.signal.model.Receive
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.concurrent.ConcurrentHashMap

open class Chat(envelope: Receive.Envelope, private val deps: ChatDeps) : BotCommand(envelope, deps.commandDeps) {

    private companion object {
        const val MAX_DIALOG_HISTORY_LENGTH = 16384 // todo: tokens
        val userChatContext = ConcurrentHashMap<String, MutableList<Chat.Message>>()
        val log = KotlinLogging.logger {}
    }

    protected val userDialogHistory: List<Chat.Message>
        get() = getDialogHistory()

    override suspend fun run() {
        val text = messageText?.takeIf { it.isNotBlank() } ?: return

        try {
            showTypingIndicator()
            initializeChatIfEmpty()
            processUserMessage(text)
            trimDialogHistory()
            processAssistantResponse()
        } catch (e: Exception) {
            log.error(e) { "chat error $userId" }
        } finally {
            showTypingIndicator(false)
        }
    }

    private fun initializeChatIfEmpty() {
        if (getDialogHistory().isEmpty())
            addMessageToHistory(Chat.Message(CHAT_ROLE_SYSTEM, deps.chatSystemPrompt))
    }

    private fun processUserMessage(text: String) {
        addMessageToHistory(Chat.Message(CHAT_ROLE_USER, text))
    }

    private fun trimDialogHistory() {
        while (getDialogHistoryLength() > MAX_DIALOG_HISTORY_LENGTH) {
            removeSecondMessageFromHistory()
        }
    }

    private suspend fun processAssistantResponse() {
        val response = deps.chatGPT.sendPrompt(getDialogHistory())
        addMessageToHistory(Chat.Message(CHAT_ROLE_ASSISTANT, response))
        replyToMessage(response)
    }

    protected fun clearDialogHistory() =
        getDialogHistory().clear()

    private fun addMessageToHistory(message: Chat.Message) =
        getDialogHistory().add(message)

    private fun removeSecondMessageFromHistory() {
        if (getDialogHistory().size > 1) getDialogHistory().removeAt(1)
    }

    private fun getDialogHistoryLength() =
        getDialogHistory().sumOf { it.content.length }

    private fun getDialogHistory() =
        userChatContext.computeIfAbsent(userId) { mutableListOf() }
}
