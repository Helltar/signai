package com.helltar.signai.signal.models

import kotlinx.serialization.Serializable

object TypingIndicator {

    @Serializable
    data class RequestData(val recipient: String)
}