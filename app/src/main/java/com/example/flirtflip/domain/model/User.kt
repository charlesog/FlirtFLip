package com.example.flirtflip.domain.model

data class User(
    val id: String,
    val email: String,
    val password: String = "",
    val profile: UserProfile
)