package com.helltar.signai.signal

import com.helltar.signai.network.HttpClient
import com.helltar.signai.signal.model.Profiles
import com.helltar.signai.signal.model.Receive
import com.helltar.signai.signal.model.Send
import com.helltar.signai.signal.model.TypingIndicator
import com.helltar.signai.signal.model.accounts.Username
import com.helltar.signai.signal.model.configuration.Settings
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import java.io.File
import java.util.*

class Signal(private val apiUrl: String, private val phoneNumber: String, private val httpClient: HttpClient) {

    private companion object {
        const val API_VERSION = "v1"
        val json = Json { ignoreUnknownKeys = true }
        val log = KotlinLogging.logger {}
    }

    suspend fun receiveEach(ignoreAttachments: Boolean = true, onReceive: suspend (Receive.Response) -> Unit) {
        val url = buildReceiveWebSocketUrl(ignoreAttachments)

        log.info { "connecting to signal receive websocket: $url" }

        httpClient.webSocket(url) { responseJson ->
            for (message in decodeReceiveResponse(responseJson)) {
                onReceive(message)
            }
        }
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

    private fun buildReceiveWebSocketUrl(ignoreAttachments: Boolean): String {
        val websocketApiUrl =
            when {
                apiUrl.startsWith("https://") -> "wss://${apiUrl.removePrefix("https://")}"
                apiUrl.startsWith("http://") -> "ws://${apiUrl.removePrefix("http://")}"
                else -> apiUrl
            }.trimEnd('/')

        return "$websocketApiUrl/$API_VERSION/receive/$phoneNumber?ignore_attachments=$ignoreAttachments"
    }

    private fun decodeReceiveResponse(responseJson: String): List<Receive.Response> =
        when (val jsonElement = json.parseToJsonElement(responseJson)) {
            is JsonArray -> json.decodeFromJsonElement(ListSerializer(Receive.Response.serializer()), jsonElement)
            else -> listOf(json.decodeFromJsonElement(Receive.Response.serializer(), jsonElement))
        }
}
