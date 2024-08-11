package com.helltar.signai.signal.model

import kotlinx.serialization.Serializable

object Send {

    @Serializable
    data class Request(
        val message: String,
        val number: String,
        val quote_author: String,
        val quote_timestamp: Long,
        val recipients: List<String>
    )
}