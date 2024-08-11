package com.helltar.signai.signal

import com.github.kittinunf.fuel.core.Response
import com.helltar.signai.signal.model.Profiles
import com.helltar.signai.signal.model.Receive
import com.helltar.signai.signal.model.Send
import com.helltar.signai.signal.model.TypingIndicator
import com.helltar.signai.signal.model.accounts.Username
import com.helltar.signai.signal.model.groups.Groups
import com.helltar.signai.utils.NetworkUtils.httpDelete
import com.helltar.signai.utils.NetworkUtils.httpGet
import com.helltar.signai.utils.NetworkUtils.httpPost
import com.helltar.signai.utils.NetworkUtils.httpPut
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*

class Signal(private val apiUrl: String, private val phoneNumber: String) {

    private companion object {
        const val API_VERSION = "v1"
    }

    private val json = Json { ignoreUnknownKeys = true }

    fun receive(timeoutSec: Int = 1, ignoreAttachments: Boolean = true): List<Receive.Response> {
        val url = "$apiUrl/$API_VERSION/receive/$phoneNumber"
        val parameters = listOf("timeout" to "$timeoutSec", "ignore_attachments" to "$ignoreAttachments")
        val responseJson = httpGet(url, parameters)
        return json.runCatching { decodeFromString<List<Receive.Response>>(responseJson) }.getOrDefault(listOf())
    }

    fun listGroups(): List<Groups.Response> {
        val url = "$apiUrl/$API_VERSION/groups/$phoneNumber"
        val responseJson = httpGet(url)
        return json.runCatching { decodeFromString<List<Groups.Response>>(responseJson) }.getOrDefault(listOf())
    }

    fun replyToMessage(text: String, replyAuthor: String, replyId: Long, recipient: String): Response {
        val url = "$apiUrl/v2/send"
        val body = json.encodeToString(Send.Request(text, phoneNumber, replyAuthor, replyId, listOf(recipient)))
        return httpPost(url, jsonBody = body)
    }

    fun updateProfile(name: String, avatar: File) {
        val url = "$apiUrl/$API_VERSION/profiles/$phoneNumber"
        val base64Avatar = Base64.getEncoder().encodeToString(avatar.readBytes())
        val body = json.encodeToString(Profiles.Request(base64Avatar, name))
        httpPut(url, body)
    }

    fun setUsername(username: String): Response {
        val url = "$apiUrl/$API_VERSION/accounts/$phoneNumber/username"
        val body = json.encodeToString(Username.Request(username))
        return httpPost(url, jsonBody = body)
    }

    fun showTypingIndicator(recipient: String, show: Boolean = true) {
        val url = "$apiUrl/$API_VERSION/typing-indicator/$phoneNumber"
        val body = json.encodeToString(TypingIndicator.Request(recipient))
        if (show) httpPut(url, body) else httpDelete(url, body)
    }
}