package com.helltar.signai.signal.models

import kotlinx.serialization.Serializable

object Send {

    @Serializable
    data class RequestData(
        val message: String,
        val number: String,
        val quote_author: String,
        val quote_timestamp: Long,
        val recipients: List<String>
    )
}