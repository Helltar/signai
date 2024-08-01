package com.helltar.signai.signal.models

import kotlinx.serialization.Serializable

object Profiles {

    @Serializable
    data class RequestData(
        val base64_avatar: String,
        val name: String,
    )
}