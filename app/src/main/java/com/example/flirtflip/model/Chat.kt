package com.example.flirtflip.model

data class Chat(
    val id: String = "",
    val userAId: String = "",
    val userBId: String = "",
    val lastMessage: String = "", //Texto o Foto Dependiendo
    val lastTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0 
)
