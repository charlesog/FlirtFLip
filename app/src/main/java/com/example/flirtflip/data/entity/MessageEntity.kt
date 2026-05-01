package com.example.flirtflip.data.entity

data class MessageEntity(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val liked: Boolean = false,
    val isRead: Boolean = false
)