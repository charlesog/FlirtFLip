package com.example.flirtflip.domain.model

data class Match(
    val id: String,
    val idUser1: String,
    val idUser2: String,
    val timestamp: Long
)
