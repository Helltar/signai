package com.helltar.signai.commands.chat

import com.helltar.signai.Strings
import com.helltar.signai.commands.ChatDeps
import com.helltar.signai.signal.model.Receive

class ChatRm(envelope: Receive.Envelope, deps: ChatDeps) : Chat(envelope, deps) {

    override suspend fun run() {
        clearDialogHistory()
        replyToMessage(Strings.CONTEXT_HAS_BEEN_REMOVED)
    }
}
