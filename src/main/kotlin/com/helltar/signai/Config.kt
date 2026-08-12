package com.helltar.signai

import io.github.cdimascio.dotenv.dotenv

object Config {

    data class BotConfig(
        val openaiAPIKey: String,
        val chatSystemPrompt: String,
        val gptModel: String,
        val userRPH: Int,
        val signalAPIUrl: String,
        val signalPhoneNumber: String
    ) {
        fun toSafeLog(): BotConfig =
            copy(openaiAPIKey = openaiAPIKey.maskApiKey())
    }

    private val dotenv = dotenv { ignoreIfMissing = true }

    val botConfig =
        BotConfig(
            openaiAPIKey = readEnv("OPENAI_API_KEY"),
            chatSystemPrompt = readEnv("CHAT_SYSTEM_PROMPT"),
            gptModel = readEnv("GPT_MODEL"),
            userRPH = readEnv("REQUESTS_PER_USER_PER_HOUR").toIntOrNull() ?: 30,
            signalAPIUrl = readEnv("SIGNAL_API_URL"),
            signalPhoneNumber = readEnv("SIGNAL_PHONE_NUMBER")
        )

    private fun readEnv(env: String) =
        dotenv[env]?.ifBlank { throw IllegalArgumentException("environment variable $env is blank") }
            ?: throw IllegalArgumentException("environment variable $env is missing")

    private fun String.maskApiKey(): String {
        if (isBlank()) return this
        if (length <= 8) return "****"
        return take(4) + "****" + takeLast(4)
    }
}
