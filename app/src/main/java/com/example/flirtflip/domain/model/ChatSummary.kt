package com.example.flirtflip.domain.model

import java.time.LocalDateTime

data class ChatSummary(
    val chatId: String,
    val otherUserName: String,
    val lastMessage: String,
    val timestamp: LocalDateTime
)