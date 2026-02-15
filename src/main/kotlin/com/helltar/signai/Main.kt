package com.helltar.signai

import com.helltar.signai.bot.Bot

suspend fun main() {
    Bot(Config.botConfig).start()
}
