package com.example.flirtflip.model

data class Message(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val text: String? = null,
    val photoUrl: String? = null,
    val audioUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val liked: Boolean = false
)

