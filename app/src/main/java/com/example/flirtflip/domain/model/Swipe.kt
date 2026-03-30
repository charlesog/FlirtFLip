package com.example.flirtflip.domain.model

data class Swipe(
    val id: String,
    val fromUserId: String,
    val toUserId: String,
    val type: SwipeType,
    val timestamp: Long
)
