package com.helltar.signai.signal.model.groups

import kotlinx.serialization.Serializable

object Groups {

    @Serializable
    data class Response(
        val id: String,
        val internal_id: String,
        val name: String
    )
}