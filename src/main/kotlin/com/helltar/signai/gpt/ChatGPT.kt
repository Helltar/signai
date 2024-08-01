package com.helltar.signai.gpt

import com.helltar.signai.gpt.models.Chat
import com.helltar.signai.gpt.models.Chat.CHAT_GPT_MODEL_4_MINI
import com.helltar.signai.utils.NetworkUtils.httpPost
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ChatGPT(private val apiKey: String) {

    private val json = Json { ignoreUnknownKeys = true }

    fun sendPrompt(messages: List<Chat.MessageData>): String {
        val url = "https://api.openai.com/v1/chat/completions"
        val headers = mapOf("Content-Type" to "application/json", "Authorization" to "Bearer $apiKey")
        val body = json.encodeToString(Chat.RequestData(CHAT_GPT_MODEL_4_MINI, messages))

        val response = httpPost(url, headers, body)
        val responseJson = response.data.decodeToString()
        val answer = json.decodeFromString<Chat.ResponseData>(responseJson).choices.first().message.content

        return answer
    }
}