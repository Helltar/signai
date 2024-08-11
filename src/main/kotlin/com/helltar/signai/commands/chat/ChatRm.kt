package com.helltar.signai.commands.chat

import com.helltar.signai.Strings
import com.helltar.signai.signal.model.Receive

class ChatRm(envelope: Receive.Envelope) : Chat(envelope) {

    override fun run() {
        clearDialogHistory()
        replyToMessage(Strings.CONTEXT_HAS_BEEN_REMOVED)
    }
}