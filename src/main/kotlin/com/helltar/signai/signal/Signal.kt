package com.helltar.signai.signal

import com.github.kittinunf.fuel.core.Response
import com.helltar.signai.signal.models.Profiles
import com.helltar.signai.signal.models.Receive
import com.helltar.signai.signal.models.Send
import com.helltar.signai.signal.models.TypingIndicator
import com.helltar.signai.signal.models.accounts.Username
import com.helltar.signai.utils.NetworkUtils.httpDelete
import com.helltar.signai.utils.NetworkUtils.httpGet
import com.helltar.signai.utils.NetworkUtils.httpPost
import com.helltar.signai.utils.NetworkUtils.httpPut
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*

class Signal(private val apiUrl: String, private val phoneNumber: String, private val recipient: String) {

    private companion object {
        const val API_VERSION = "v1"
    }

    private val json = Json { ignoreUnknownKeys = true }

    fun receive(): List<Receive.ResponseData> {
        val url = "$apiUrl/$API_VERSION/receive/$phoneNumber"
        val responseJson = httpGet(url)
        return json.runCatching { decodeFromString<List<Receive.ResponseData>>(responseJson) }.getOrDefault(listOf())
    }

    fun replyToMessage(text: String, replyAuthor: String, replyId: Long): Response {
        val url = "$apiUrl/v2/send"
        val body = json.encodeToString(Send.RequestData(text, phoneNumber, replyAuthor, replyId, listOf(recipient)))
        return httpPost(url, jsonBody = body)
    }

    fun updateProfile(name: String, avatar: File) {
        val url = "$apiUrl/$API_VERSION/profiles/$phoneNumber"
        val base64Avatar = Base64.getEncoder().encodeToString(avatar.readBytes())
        val body = json.encodeToString(Profiles.RequestData(base64Avatar, name))
        httpPut(url, body)
    }

    fun setUsername(username: String): Response {
        val url = "$apiUrl/$API_VERSION/accounts/$phoneNumber/username"
        val body = json.encodeToString(Username.RequestData(username))
        return httpPost(url, jsonBody = body)
    }

    fun showTypingIndicator(show: Boolean = true) {
        val url = "$apiUrl/$API_VERSION/typing-indicator/$phoneNumber"
        val body = json.encodeToString(TypingIndicator.RequestData(recipient))
        if (show) httpPut(url, body) else httpDelete(url, body)
    }
}