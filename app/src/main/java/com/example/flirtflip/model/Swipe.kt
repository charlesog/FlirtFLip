package com.example.flirtflip.model

data class Swipe(
    val fromUserId: String = "",
    val toUserId: String = "",
    val type: SwipeType = SwipeType.DISLIKE,
    val timeMatch: Long = System.currentTimeMillis()
)
