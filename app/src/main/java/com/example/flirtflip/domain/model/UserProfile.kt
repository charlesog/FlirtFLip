package com.example.flirtflip.domain.model

data class UserProfile(
    val name: String,
    val firstSurname: String,
    val secondSurname: String,
    val age: Int,
    val gender: Gender,
    val bio: String,
    val hobbies: List<Hobby>,
    val imageUrl: List<String>,
    val job: String? = null
)
