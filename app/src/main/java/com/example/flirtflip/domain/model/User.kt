package com.example.flirtflip.domain.model

data class User(
    val id: String,
    val email: String,
    val profile: UserProfile
)