package com.helltar.signai.commands.chat

import com.helltar.signai.EnvConfig.chatSystemPrompt
import com.helltar.signai.commands.BotCommand
import com.helltar.signai.gpt.ChatGPT
import com.helltar.signai.gpt.model.Chat
import com.helltar.signai.gpt.model.Chat.CHAT_ROLE_ASSISTANT
import com.helltar.signai.gpt.model.Chat.CHAT_ROLE_SYSTEM
import com.helltar.signai.gpt.model.Chat.CHAT_ROLE_USER
import com.helltar.signai.signal.model.Receive
import org.slf4j.LoggerFactory

open class Chat(envelope: Receive.Envelope) : BotCommand(envelope) {

    private companion object {
        const val MAX_USER_DIALOG_HISTORY_LENGTH = 10000
        val userChatContextMap = hashMapOf<String, MutableList<Chat.Message>>()
    }

    protected val userChatDialogHistory: List<Chat.Message>
        get() = dialogHistory()

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run() {
        val text = messageText.takeIf { it?.isNotBlank() ?: return } ?: return

        try {
            showTypingIndicator()

            if (dialogHistory().isEmpty()) {
                val systemPrompt = Chat.Message(CHAT_ROLE_SYSTEM, chatSystemPrompt)
                addMessageToHistory(systemPrompt)
            }

            addMessageToHistory(Chat.Message(CHAT_ROLE_USER, text))

            var dialogLength = dialogHistoryLengthSum()

            if (dialogLength > MAX_USER_DIALOG_HISTORY_LENGTH)
                while (dialogLength > MAX_USER_DIALOG_HISTORY_LENGTH) {
                    removeSecondMessageFromHistory()
                    dialogLength = dialogHistoryLengthSum()
                }

            val answer = ChatGPT.sendPrompt(dialogHistory())

            addMessageToHistory(Chat.Message(CHAT_ROLE_ASSISTANT, answer))

            replyToMessage(answer)
        } catch (e: Exception) {
            log.error(e.message)
        } finally {
            showTypingIndicator(false)
        }
    }

    protected fun clearDialogHistory() =
        dialogHistory().clear()

    private fun addMessageToHistory(messageData: Chat.Message) =
        dialogHistory().add(messageData)

    private fun removeSecondMessageFromHistory() {
        if (dialogHistory().size > 1) dialogHistory().removeAt(1)
    }

    private fun dialogHistoryLengthSum() =
        dialogHistory().sumOf { it.content.length }

    private fun dialogHistory() =
        userChatContextMap.getOrPut(userId) { mutableListOf() }
}