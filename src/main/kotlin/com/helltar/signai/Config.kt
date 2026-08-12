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

    private const val DEFAULT_CHAT_SYSTEM_PROMPT =
        "You are a participant in a Signal chat. Answer in the language you were addressed in, " +
            "keep it to a few sentences unless more is asked for, and write plain text — Signal renders no markdown."

    private val dotenv = dotenv { ignoreIfMissing = true }

    val botConfig =
        BotConfig(
            openaiAPIKey = readEnv("OPENAI_API_KEY"),
            chatSystemPrompt = readEnvOrNull("CHAT_SYSTEM_PROMPT") ?: DEFAULT_CHAT_SYSTEM_PROMPT,
            gptModel = readEnv("GPT_MODEL"),
            userRPH = readEnvOrNull("REQUESTS_PER_USER_PER_HOUR")?.toIntOrNull() ?: 30,
            signalAPIUrl = readEnvOrNull("SIGNAL_API_URL") ?: "http://signal-cli-rest-api:8080",
            signalPhoneNumber = readEnv("SIGNAL_PHONE_NUMBER")
        )

    private fun readEnv(env: String) =
        dotenv[env]?.ifBlank { throw IllegalArgumentException("environment variable $env is blank") }
            ?: throw IllegalArgumentException("environment variable $env is missing")

    private fun readEnvOrNull(env: String) =
        dotenv[env]?.takeIf { it.isNotBlank() }

    private fun String.maskApiKey(): String {
        if (isBlank()) return this
        if (length <= 8) return "****"
        return take(4) + "****" + takeLast(4)
    }
}
