package com.example.flirtflip.model

data class User(
    val id: String = "",
    val name: String = "",
    val firstSurname: String = "",
    val secondSurname: String = "",
    val email: String = "",
    val age: Int = 0,
    //'M' = Masculino, 'F' = Femenino, 'U' = Undefined
    val gender: Char = 'U',
    val profile: Profile = Profile()
)
