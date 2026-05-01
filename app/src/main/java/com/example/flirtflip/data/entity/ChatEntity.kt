package com.example.flirtflip.data.entity

data class ChatEntity(
    val id: String = "",
    val userIds: List<String> = emptyList(),
    val lastMessage: String? = null,
    val lastMessageTimestamp: Long? = null,
    val isActive: Boolean = true
)