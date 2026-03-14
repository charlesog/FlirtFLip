package com.example.flirtflip.domain.model

import java.time.LocalDateTime

data class Match(
    val id: String,
    val idUser1: String,
    val idUser2: String,
    val timestamp: LocalDateTime
)
