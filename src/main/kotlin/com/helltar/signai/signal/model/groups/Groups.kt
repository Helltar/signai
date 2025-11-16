package com.helltar.signai.signal.model.groups

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object Groups {

    @Serializable
    data class Response(
        val id: String,

        @SerialName("internal_id")
        val internalId: String,

        val name: String
    )
}
