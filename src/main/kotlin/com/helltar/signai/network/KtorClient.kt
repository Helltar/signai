package com.helltar.signai.network

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*

object KtorClient : NetworkClient {

    private val httpClient =
        HttpClient(CIO) {
            expectSuccess = true

            install(HttpTimeout) {
                requestTimeoutMillis = 180_000
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 180_000
            }
        }

    private val wsClient =
        HttpClient(CIO) {
            expectSuccess = true

            install(HttpTimeout) {
                connectTimeoutMillis = 30_000
            }

            install(WebSockets) {
                pingIntervalMillis = 20_000
            }
        }

    private val log = KotlinLogging.logger {}

    override suspend fun get(url: String, parameters: List<Pair<String, String>>): String {
        val response =
            httpClient.get(url) {
                url {
                    parameters.forEach { (key, value) ->
                        this.parameters.append(key, value)
                    }
                }
            }

        val body = response.bodyAsText()

        log.debug { "${response.request.url} --> $body" }

        return body
    }

    override suspend fun post(url: String, headers: Map<String, String>, body: String): String {
        val response =
            httpClient.post(url) {
                headers.forEach { (key, value) -> header(key, value) }
                contentType(ContentType.Application.Json)
                setBody(body)
            }

        return response.bodyAsText()
    }

    override suspend fun put(url: String, body: String): String {
        val response =
            httpClient.put(url) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }

        return response.bodyAsText()
    }

    override suspend fun delete(url: String, body: String): String {
        val response =
            httpClient.delete(url) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }

        return response.bodyAsText()
    }

    override suspend fun webSocket(url: String, onTextFrame: suspend (String) -> Unit) {
        wsClient.webSocket(urlString = url) {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    log.debug { "$url --> $text" }
                    onTextFrame(text)
                }
            }
        }
    }
}
