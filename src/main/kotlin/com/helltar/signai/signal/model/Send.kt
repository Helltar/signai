package com.helltar.signai.signal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object Send {

    @Serializable
    data class Request(
        val message: String,
        val number: String,

        @SerialName("quote_author")
        val quoteAuthor: String,

        @SerialName("quote_timestamp")
        val quoteTimestamp: Long,

        val recipients: List<String>
    )
}
