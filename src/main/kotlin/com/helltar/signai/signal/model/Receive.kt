package com.helltar.signai.signal.model

import kotlinx.serialization.Serializable

object Receive {

    @Serializable
    data class Response(
        val envelope: Envelope,
        val account: String,
    )

    @Serializable
    data class Envelope(
        val source: String,
        val timestamp: Long,
        val dataMessage: DataMessage? = null
    )

    @Serializable
    data class DataMessage(
        val timestamp: Long,
        var message: String? = null,
        val quote: Quote? = null,
        val groupInfo: GroupInfo? = null
    )

    @Serializable
    data class Quote(
        val id: Long,
        val author: String,
        val authorNumber: String? = null,
    )

    @Serializable
    data class GroupInfo(
        val groupId: String
    )
}