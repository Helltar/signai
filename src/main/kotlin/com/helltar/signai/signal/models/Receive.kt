package com.helltar.signai.signal.models

import kotlinx.serialization.Serializable

object Receive {

    @Serializable
    data class ResponseData(
        val envelope: EnvelopeData,
        val account: String,
    )

    @Serializable
    data class EnvelopeData(
        val source: String,
        val timestamp: Long,
        val dataMessage: MessageData? = null
    )

    @Serializable
    data class MessageData(
        val timestamp: Long,
        val message: String? = null,
        val quote: QuoteData? = null
    )

    @Serializable
    data class QuoteData(
        val id: Long,
        val author: String,
        val authorNumber: String? = null,
    )
}