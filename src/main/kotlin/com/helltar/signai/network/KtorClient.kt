package com.helltar.signai.network

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

object KtorClient : HttpClient {

    private const val TIMEOUT = 180_000L

    private val client =
        HttpClient(CIO) {
            expectSuccess = true

            install(HttpTimeout) {
                requestTimeoutMillis = TIMEOUT
                connectTimeoutMillis = TIMEOUT
                socketTimeoutMillis = TIMEOUT
            }
        }

    private val log = KotlinLogging.logger {}

    override suspend fun get(url: String, parameters: List<Pair<String, String>>): String {
        val response =
            client.get(url) {
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
            client.post(url) {
                headers.forEach { (key, value) -> header(key, value) }
                contentType(ContentType.Application.Json)
                setBody(body)
            }

        return response.bodyAsText()
    }

    override suspend fun put(url: String, body: String): String {
        val response =
            client.put(url) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }

        return response.bodyAsText()
    }

    override suspend fun delete(url: String, body: String): String {
        val response =
            client.delete(url) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }

        return response.bodyAsText()
    }
}
