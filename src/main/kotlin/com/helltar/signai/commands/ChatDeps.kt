package com.helltar.signai.commands

import com.helltar.signai.gpt.ChatGPT

data class ChatDeps(
    val commandDeps: CommandDeps,
    val chatSystemPrompt: String,
    val chatGPT: ChatGPT
)
