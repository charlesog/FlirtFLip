package com.example.flirtflip.data.entity

data class MatchEntity(
    val id: String = "",
    val idUser1: String = "",
    val idUser2: String = "",
    val users: List<String> = emptyList(),
    val timestamp: Long = 0L
)