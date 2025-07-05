package com.helltar.signai.commands.chat

import com.helltar.signai.Config.chatSystemPrompt
import com.helltar.signai.commands.BotCommand
import com.helltar.signai.gpt.ChatGPT
import com.helltar.signai.gpt.model.Chat
import com.helltar.signai.gpt.model.Chat.CHAT_ROLE_ASSISTANT
import com.helltar.signai.gpt.model.Chat.CHAT_ROLE_SYSTEM
import com.helltar.signai.gpt.model.Chat.CHAT_ROLE_USER
import com.helltar.signai.signal.model.Receive
import io.github.oshai.kotlinlogging.KotlinLogging

open class Chat(envelope: Receive.Envelope) : BotCommand(envelope) {

    private companion object {
        const val MAX_DIALOG_HISTORY_LENGTH = 15000
        val userChatContext = hashMapOf<String, MutableList<Chat.Message>>()
        val log = KotlinLogging.logger {}
    }

    protected val userDialogHistory: List<Chat.Message>
        get() = getDialogHistory()

    override fun run() {
        val text = messageText?.takeIf { it.isNotBlank() } ?: return

        try {
            showTypingIndicator()
            initializeChatIfEmpty()
            processUserMessage(text)
            trimDialogHistory()
            processAssistantResponse()
        } catch (e: Exception) {
            log.error { e.message }
        } finally {
            showTypingIndicator(false)
        }
    }

    private fun initializeChatIfEmpty() {
        if (getDialogHistory().isNotEmpty()) return
        addMessageToHistory(Chat.Message(CHAT_ROLE_SYSTEM, chatSystemPrompt))
    }

    private fun processUserMessage(text: String) {
        addMessageToHistory(Chat.Message(CHAT_ROLE_USER, text))
    }

    private fun trimDialogHistory() {
        while (getDialogHistoryLength() > MAX_DIALOG_HISTORY_LENGTH) {
            removeSecondMessageFromHistory()
        }
    }

    private fun processAssistantResponse() {
        val response = ChatGPT.sendPrompt(getDialogHistory())
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
        userChatContext.getOrPut(userId) { mutableListOf() }
}
