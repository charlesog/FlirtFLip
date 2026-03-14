package com.example.flirtflip.domain.model

import java.time.LocalDateTime

data class Chat(
    val id: String,
    val userIds: List<String>,
    val lastMessage: String?,
    val lastMessageTimestamp: LocalDateTime?,
    val isActive: Boolean = true
)
