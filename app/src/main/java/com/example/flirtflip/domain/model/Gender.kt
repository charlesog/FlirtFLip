package com.example.flirtflip.domain.model

enum class Gender {
    MALE, FEMALE, UNDEFINED;

    fun isInvalid(): Boolean { return this == UNDEFINED }
}