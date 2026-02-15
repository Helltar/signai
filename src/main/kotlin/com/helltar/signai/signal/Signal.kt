package com.helltar.signai.signal

import com.helltar.signai.network.HttpClient
import com.helltar.signai.signal.model.Profiles
import com.helltar.signai.signal.model.Receive
import com.helltar.signai.signal.model.Send
import com.helltar.signai.signal.model.TypingIndicator
import com.helltar.signai.signal.model.accounts.Username
import com.helltar.signai.signal.model.configuration.Settings
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*

class Signal(private val apiUrl: String, private val phoneNumber: String, private val httpClient: HttpClient) {

    private companion object {
        const val API_VERSION = "v1"
        val json = Json { ignoreUnknownKeys = true }
        val log = KotlinLogging.logger {}
    }

    suspend fun receive(timeoutSec: Int = 1, ignoreAttachments: Boolean = true): List<Receive.Response> {
        val url = "$apiUrl/$API_VERSION/receive/$phoneNumber"
        val parameters = listOf("timeout" to "$timeoutSec", "ignore_attachments" to "$ignoreAttachments")
        val responseJson = httpClient.get(url, parameters)
        return json.decodeFromString(responseJson)
    }

    suspend fun replyToMessage(text: String, replyAuthor: String, replyId: Long, recipient: String): String {
        val url = "$apiUrl/v2/send"
        val body = json.encodeToString(Send.Request(text, phoneNumber, replyAuthor, replyId, listOf(recipient)))
        log.debug { "message recipient: $recipient" }
        return httpClient.post(url, body = body)
    }

    suspend fun updateProfile(name: String, avatar: File) {
        val url = "$apiUrl/$API_VERSION/profiles/$phoneNumber"
        val base64Avatar = Base64.getEncoder().encodeToString(avatar.readBytes())
        val body = json.encodeToString(Profiles.Request(base64Avatar, name))
        httpClient.put(url, body)
    }

    suspend fun setUsername(username: String): String {
        val url = "$apiUrl/$API_VERSION/accounts/$phoneNumber/username"
        val body = json.encodeToString(Username.Request(username))
        return httpClient.post(url, body = body)
    }

    suspend fun showTypingIndicator(recipient: String, show: Boolean = true) {
        val url = "$apiUrl/$API_VERSION/typing-indicator/$phoneNumber"
        val body = json.encodeToString(TypingIndicator.Request(recipient))
        if (show) httpClient.put(url, body) else httpClient.delete(url, body)
    }

    suspend fun setAccountSettings(trustMode: String): String {
        val url = "$apiUrl/$API_VERSION/configuration/$phoneNumber/settings"
        val body = json.encodeToString(Settings.Request(trustMode))
        return httpClient.post(url, body = body)
    }
}
