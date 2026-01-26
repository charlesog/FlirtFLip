package com.example.flirtflip.model

data class Match(
    val id: Int = 0,
    val userAId: Int = 0,
    val userBId: Int = 0,
    // Tiempo en milisegundos de cuando se hizo el match
    val timeMatch: Long = System.currentTimeMillis()
)
