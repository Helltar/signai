package com.helltar.signai

import io.github.cdimascio.dotenv.dotenv

object Config {

    val openaiAPIKey = readEnv("OPENAI_API_KEY")
    val chatSystemPrompt = readEnv("CHAT_SYSTEM_PROMPT")
    val gptModel = readEnv("GPT_MODEL")
    val userRPH = readEnv("REQUESTS_PER_USER_PER_HOUR").toIntOrNull() ?: 30

    val signalAPIUrl = readEnv("SIGNAL_API_URL")
    val signalPhoneNumber = readEnv("SIGNAL_PHONE_NUMBER")

    val botName = readEnv("BOT_NAME")
    val botUsername = readEnv("BOT_USERNAME")

    private fun readEnv(env: String) =
        dotenv { ignoreIfMissing = true }[env].ifBlank { throw IllegalArgumentException("$env env. is blank") }
            ?: throw IllegalArgumentException("error when read $env env.")
}
