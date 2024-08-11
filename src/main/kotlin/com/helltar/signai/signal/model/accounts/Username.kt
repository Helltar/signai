package com.helltar.signai.signal.model.accounts

import kotlinx.serialization.Serializable

object Username {

    @Serializable
    data class Request(
        val username: String
    )
}