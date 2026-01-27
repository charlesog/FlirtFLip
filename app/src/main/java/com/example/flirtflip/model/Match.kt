package com.example.flirtflip.model

data class Match(
    val id: String = "",
    val userAId: String = "",
    val userBId: String = "",
    // Tiempo en milisegundos de cuando se hizo el match
    val timeMatch: Long = System.currentTimeMillis()
)
