package com.helltar.signai.signal.model

import kotlinx.serialization.Serializable

object TypingIndicator {

    @Serializable
    data class Request(
        val recipient: String
    )
}