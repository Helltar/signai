package com.helltar.signai.gpt

import com.helltar.signai.gpt.model.Chat
import com.helltar.signai.network.HttpClient
import kotlinx.serialization.json.Json

class ChatGPT(private val apiKey: String, private val model: String, private val httpClient: HttpClient) {

    private companion object {
        val json = Json { ignoreUnknownKeys = true }
    }

    suspend fun sendPrompt(messages: List<Chat.Message>): String {
        val url = "https://api.openai.com/v1/chat/completions"
        val headers = mapOf("Content-Type" to "application/json", "Authorization" to "Bearer $apiKey")
        val body = json.encodeToString(Chat.Request(model, messages))
        val responseJson = httpClient.post(url, headers, body)
        return json.decodeFromString<Chat.Response>(responseJson).choices.first().message.content
    }
}
