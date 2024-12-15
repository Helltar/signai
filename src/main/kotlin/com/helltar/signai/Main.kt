package com.helltar.signai

import com.helltar.signai.Config.botName
import com.helltar.signai.Config.botUsername
import com.helltar.signai.bot.Bot
import java.io.File

suspend fun main() {
    val avatar = File("data/avatar.jpg")
    Bot(botUsername, botName, avatar).run().join()
}
