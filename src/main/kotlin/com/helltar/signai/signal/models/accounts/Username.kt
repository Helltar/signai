package com.helltar.signai.signal.models.accounts

import kotlinx.serialization.Serializable

object Username {

    @Serializable
    data class RequestData(val username: String)

    @Serializable
    data class ResponseData(
        val username: String,
        val username_link: String,
    )
}