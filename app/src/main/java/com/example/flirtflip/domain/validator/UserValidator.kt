package com.example.flirtflip.domain.validator

import com.example.flirtflip.domain.model.User

object UserValidator {
    fun validateOnlyAuth(email: String, passwordRaw: String): ValidationResult {
        return when {
            !email.contains("@") -> ValidationResult.Invalid("Invalid email format")
            passwordRaw.length < 8 -> ValidationResult.Invalid("Password must be at least 8 characters long")
            else -> ValidationResult.Valid
        }
    }

    fun validateFullProfile(user: User): ValidationResult {
        return when {
            user.profile.name.isBlank() -> ValidationResult.Invalid("Name cannot be empty")
            user.profile.age < 18 -> ValidationResult.Invalid("Age must be at least 18")
            else -> ValidationResult.Valid
        }
    }
}

sealed class ValidationResult{
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}