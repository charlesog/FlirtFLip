package com.example.flirtflip.model

data class Profile(
    val bio: String = "",
    val hobbies: List<String> = emptyList(),
    val photos: List<String> = emptyList()
)