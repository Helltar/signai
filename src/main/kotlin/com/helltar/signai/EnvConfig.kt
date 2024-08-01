package com.helltar.signai

import io.github.cdimascio.dotenv.dotenv

object EnvConfig {

    val openaiAPIKey = readEnv("OPENAI_API_KEY")
    val chatSystemPrompt = readEnv("CHAT_SYSTEM_PROMPT")

    val signalAPIUrl = readEnv("SIGNAL_API_URL")
    val signalPhoneNumber = readEnv("SIGNAL_PHONE_NUMBER")
    val signalGroupID = readEnv("SIGNAL_GROUP_ID")

    val botName = readEnv("BOT_NAME")
    val botUsername = readEnv("BOT_USERNAME")

    private fun readEnv(env: String) =
        dotenv { ignoreIfMissing = true }[env].ifBlank { throw IllegalArgumentException("$env env. is blank") }
            ?: throw IllegalArgumentException("error when read $env env.")
}