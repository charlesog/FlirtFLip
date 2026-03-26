package com.example.flirtflip.data.entity

data class UserEntity(
    val id: String,
    val email: String,
    val password: String = "",
    val profile: UserProfileEntity
)
