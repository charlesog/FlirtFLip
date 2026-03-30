package com.example.flirtflip.data.entity

data class UserProfileEntity(
    val name: String = "",
    val firstSurname: String = "",
    val secondSurname: String = "",
    val age: Int = 0,
    val gender: String = "UNDEFINIED",
    val bio: String = "",
    val hobbies: List<String> = emptyList(),
    val mainPhotoUrl: String = "",
    val galleryPhotoUrls: List<String> = emptyList(),
    val job: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)
