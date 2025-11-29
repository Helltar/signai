package com.helltar.signai.signal.model.configuration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object Settings {

    @Serializable
    data class Request(
        @SerialName("trust_mode")
        val trustMode: String
    )
}
