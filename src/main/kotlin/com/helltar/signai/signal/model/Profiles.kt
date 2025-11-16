package com.helltar.signai.signal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object Profiles {

    @Serializable
    data class Request(

        @SerialName("base64_avatar")
        val base64Avatar: String,

        val name: String,
    )
}
