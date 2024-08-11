package com.helltar.signai.signal.model

import kotlinx.serialization.Serializable

object Profiles {

    @Serializable
    data class Request(
        val base64_avatar: String,
        val name: String,
    )
}