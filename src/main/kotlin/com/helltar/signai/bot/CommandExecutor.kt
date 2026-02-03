package com.helltar.signai.bot

import com.helltar.signai.Strings
import com.helltar.signai.commands.BotCommand
import com.helltar.signai.commands.chat.Chat
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.ceil

class CommandExecutor(private val scope: CoroutineScope, private val userRequestsPerHour: Int) {

    private val requestsMap = hashMapOf<String, Job>()
    private val requestTimestamps = hashMapOf<String, MutableList<Long>>()

    private companion object {
        const val LIMIT_WINDOW = 3600_000L
        val log = KotlinLogging.logger {}
    }

    private sealed interface LaunchResult {
        data object Success : LaunchResult
        data object Busy : LaunchResult
        data class RateLimited(val waitSeconds: Long) : LaunchResult
    }

    fun execute(botCommand: BotCommand) {
        val key = botCommand.envelope.source
        val isChatCommand = botCommand is Chat

        val result = tryLaunch(key, checkRateLimit = isChatCommand) { botCommand.run() }

        when (result) {
            is LaunchResult.Success -> {}
            is LaunchResult.Busy -> scope.launch { botCommand.replyToMessage(Strings.MANY_REQUEST) }
            is LaunchResult.RateLimited -> scope.launch { botCommand.replyToMessage(Strings.SLOWMODE.format(result.waitSeconds)) }
        }
    }

    private fun tryLaunch(key: String, checkRateLimit: Boolean, block: suspend () -> Unit): LaunchResult {
        log.debug { "try launch --> $key" }

        if (requestsMap[key]?.isActive == true)
            return LaunchResult.Busy

        if (checkRateLimit) {
            val timestamps = requestTimestamps.getOrPut(key) { mutableListOf() }
            val currentTime = System.currentTimeMillis()
            val windowStart = currentTime - LIMIT_WINDOW

            timestamps.removeAll { it < windowStart }

            if (timestamps.size >= userRequestsPerHour) {
                val oldestRequestTime = timestamps.first()
                val unblockTime = oldestRequestTime + LIMIT_WINDOW
                val waitMs = unblockTime - currentTime
                val waitSeconds = if (waitMs > 0) ceil(waitMs / 1000.0).toLong() else 1L
                log.debug { "rate limit for $key. wait: ${waitSeconds}s" }
                return LaunchResult.RateLimited(waitSeconds)
            }

            timestamps.add(currentTime)

            log.debug { "chat launch --> $key (requests: ${timestamps.size} / $userRequestsPerHour)" }
        }

        requestsMap[key] = scope.launch { block() }

        return LaunchResult.Success
    }
}
