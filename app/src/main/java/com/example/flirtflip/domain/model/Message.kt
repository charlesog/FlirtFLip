package com.example.flirtflip.domain.model

import java.time.LocalDateTime

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val text: String,
    val timestamp: LocalDateTime,
    val liked: Boolean,
    val isRead: Boolean,
    val messageType: MessageType = MessageType.TEXT
)
