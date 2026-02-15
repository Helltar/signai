package com.helltar.signai.commands

import com.helltar.signai.Config.signalAPIUrl
import com.helltar.signai.Config.signalPhoneNumber
import com.helltar.signai.signal.Signal
import com.helltar.signai.signal.model.Receive
import java.util.*

abstract class BotCommand(val envelope: Receive.Envelope) {

    private companion object {
        val BASE64_ENCODER: Base64.Encoder = Base64.getEncoder()
    }

    abstract suspend fun run()

    private val signal = Signal(signalAPIUrl, signalPhoneNumber)

    private val messageId = envelope.timestamp
    private val groupId = envelope.dataMessage?.groupInfo?.groupId // internal_id

    protected val userId = envelope.source
    protected val messageText = envelope.dataMessage?.message

    suspend fun replyToMessage(text: String) =
        signal.replyToMessage(text, userId, messageId, recipient())

    protected suspend fun showTypingIndicator(show: Boolean = true) =
        signal.showTypingIndicator(recipient(), show)

    private fun recipient(): String = // internal_id --> id
        groupId?.let { "group." + BASE64_ENCODER.encodeToString(it.toByteArray()) } ?: envelope.source
}
