package com.example.flirtflip.model

data class User(
    val id: Int = 0,
    val name: String = "",
    val firstSurname: String = "",
    val secondSurname: String = "",
    val email: String = "",
    val age: Int = 18,
    //'M' = Masculino, 'F' = Femenino, 'U' = Undefined
    val gender: Char = 'U',
    val bio: String = "",
    val hobbies: List<String> = emptyList(),
    val profilePhoto: String = ""
)
