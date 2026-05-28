package com.example.flirtflip.data.entity

data class SwipeEntity(
    val id: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val type: String = "DISLIKE",
    val timestamp: Long = 0L
)