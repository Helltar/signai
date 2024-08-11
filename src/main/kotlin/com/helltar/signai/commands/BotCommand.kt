package com.helltar.signai.commands

import com.helltar.signai.EnvConfig.signalAPIUrl
import com.helltar.signai.EnvConfig.signalPhoneNumber
import com.helltar.signai.signal.Signal
import com.helltar.signai.signal.model.Receive

abstract class BotCommand(val envelope: Receive.Envelope) {

    abstract fun run()

    private val signal = Signal(signalAPIUrl, signalPhoneNumber)

    private val messageId = envelope.timestamp
    private val groupId = envelope.dataMessage?.groupInfo?.groupId
    private val recipient = groupId?.let { signal.listGroups().find { group -> it == group.internal_id }?.id } ?: envelope.source

    protected val userId = envelope.source
    protected val messageText = envelope.dataMessage?.message

    fun replyToMessage(text: String) =
        signal.replyToMessage(text, userId, messageId, recipient)

    protected fun showTypingIndicator(show: Boolean = true) =
        signal.showTypingIndicator(recipient, show)
}