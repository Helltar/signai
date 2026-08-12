package com.helltar.signai

import io.github.cdimascio.dotenv.dotenv
import java.io.File

object Config {

    class Avatar(val bytes: ByteArray, private val source: String) {
        override fun toString() = source
    }

    data class BotConfig(
        val openaiAPIKey: String,
        val chatSystemPrompt: String,
        val gptModel: String,
        val userRPH: Int,
        val signalAPIUrl: String,
        val signalPhoneNumber: String,
        val botName: String,
        val botUsername: String,
        val avatar: Avatar
    ) {
        fun toSafeLog(): BotConfig =
            copy(openaiAPIKey = openaiAPIKey.maskApiKey())
    }

    private const val PACKAGED_AVATAR = "/avatar.jpg"

    private val dotenv = dotenv { ignoreIfMissing = true }

    val botConfig =
        BotConfig(
            openaiAPIKey = readEnv("OPENAI_API_KEY"),
            chatSystemPrompt = readEnv("CHAT_SYSTEM_PROMPT"),
            gptModel = readEnv("GPT_MODEL"),
            userRPH = readEnv("REQUESTS_PER_USER_PER_HOUR").toIntOrNull() ?: 30,
            signalAPIUrl = readEnv("SIGNAL_API_URL"),
            signalPhoneNumber = readEnv("SIGNAL_PHONE_NUMBER"),
            botName = readEnv("BOT_NAME"),
            botUsername = readEnv("BOT_USERNAME"),
            avatar = readAvatar()
        )

    private fun readAvatar(): Avatar {
        val path = readEnvOrNull("AVATAR_FILE")

        if (path != null) {
            val file = File(path)

            if (file.canRead()) return Avatar(file.readBytes(), path)

            throw IllegalArgumentException("AVATAR_FILE is set to [$path], which cannot be read")
        }

        val packaged =
            Config::class.java.getResourceAsStream(PACKAGED_AVATAR)?.use { it.readBytes() }
                ?: throw IllegalStateException("$PACKAGED_AVATAR is missing from the jar")

        return Avatar(packaged, "packaged$PACKAGED_AVATAR")
    }

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
