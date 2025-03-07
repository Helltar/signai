package com.helltar.signai.gpt.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object Chat {

    const val CHAT_GPT_MODEL_4 = "gpt-4o"

    const val CHAT_ROLE_USER = "user"
    const val CHAT_ROLE_ASSISTANT = "assistant"
    const val CHAT_ROLE_SYSTEM = "system"

    @Serializable
    data class Request(
        val model: String,
        val messages: List<Message>
    )

    @Serializable
    data class Response(
        val model: String,
        val choices: List<Choice>,
        val usage: Usage
    )

    @Serializable
    data class Message(
        val role: String,
        val content: String
    )

    @Serializable
    data class Choice(
        val index: Int,
        val message: Message,

        @SerialName("finish_reason")
        val finishReason: String
    )

    @Serializable
    data class Usage(
        @SerialName("prompt_tokens")
        val promptTokens: Int,

        @SerialName("completion_tokens")
        val completionTokens: Int,

        @SerialName("total_tokens")
        val totalTokens: Int
    )
}
