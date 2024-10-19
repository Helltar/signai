package com.helltar.signai.gpt

import com.helltar.signai.EnvConfig.openaiAPIKey
import com.helltar.signai.gpt.model.Chat
import com.helltar.signai.gpt.model.Chat.CHAT_GPT_MODEL_4
import com.helltar.signai.utils.NetworkUtils.httpPost
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ChatGPT {

    private val json = Json { ignoreUnknownKeys = true }

    fun sendPrompt(messages: List<Chat.Message>): String {
        val url = "https://api.openai.com/v1/chat/completions"
        val headers = mapOf("Content-Type" to "application/json", "Authorization" to "Bearer $openaiAPIKey")
        val body = json.encodeToString(Chat.Request(CHAT_GPT_MODEL_4, messages))

        val response = httpPost(url, headers, body)
        val responseJson = response.data.decodeToString()
        val answer = json.decodeFromString<Chat.Response>(responseJson).choices.first().message.content

        return answer.cleanMarkdown()
    }

    private fun String.cleanMarkdown() =
        this.replace("\\*\\*(.*?)\\*\\*".toRegex(), "$1")
            .replace("\\*(.*?)\\*".toRegex(), "$1")
            .replace("### (.*?)\\n".toRegex(), "$1\n")
            .replace("## (.*?)\\n".toRegex(), "$1\n")
            .replace("# (.*?)\\n".toRegex(), "$1\n")
}
