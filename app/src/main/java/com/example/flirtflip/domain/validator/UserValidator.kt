package com.example.flirtflip.domain.validator

import com.example.flirtflip.domain.model.UserProfile

object UserValidator {
    fun validateOnlyAuth(email: String, passwordRaw: String): ValidationResult {
        return when {
            !email.contains("@") -> ValidationResult.Invalid("Invalid email format")
            passwordRaw.length < 8 -> ValidationResult.Invalid("Password must be at least 8 characters long")
            else -> ValidationResult.Valid
        }
    }

    fun validateFullProfile(userProfile: UserProfile): ValidationResult {
        return when {
            userProfile.name.isBlank() -> ValidationResult.Invalid("Name cannot be empty")
            userProfile.age < 18 -> ValidationResult.Invalid("Age must be at least 18")
            userProfile.gender.isInvalid() -> ValidationResult.Invalid("Gender cannot be undefined")
            userProfile.imageUrl.isEmpty() -> ValidationResult.Invalid("At least one photo is required")
            userProfile.imageUrl.size > 6 -> ValidationResult.Invalid("Maximum of 6 photos allowed")
            else -> ValidationResult.Valid
        }
    }
}
