package com.helltar.signai.gpt

import com.helltar.signai.Config
import com.helltar.signai.Config.openaiAPIKey
import com.helltar.signai.gpt.model.Chat
import com.helltar.signai.network.KtorClient
import kotlinx.serialization.json.Json

object ChatGPT {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun sendPrompt(messages: List<Chat.Message>): String {
        val url = "https://api.openai.com/v1/chat/completions"
        val headers = mapOf("Content-Type" to "application/json", "Authorization" to "Bearer $openaiAPIKey")
        val body = json.encodeToString(Chat.Request(Config.gptModel, messages))
        val responseJson = KtorClient.post(url, headers, body)
        return json.decodeFromString<Chat.Response>(responseJson).choices.first().message.content
    }
}
