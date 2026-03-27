package com.example.flirtflip.domain.validator

import com.example.flirtflip.domain.model.User

object UserValidator {
    fun validateRegistration(user: User): ValidationResult {
        return when {
            user.profile.age < 18 -> ValidationResult.Invalid("Age must be at least 18")
            user.profile.name.isBlank() -> ValidationResult.Invalid("Name cannot be empty")
            user.password.length < 6 -> ValidationResult.Invalid("Password must be at least 6 characters long")
            !user.email.contains("@") -> ValidationResult.Invalid("Invalid email format")
            else -> ValidationResult.Valid
        }
    }
}

sealed class ValidationResult{
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}