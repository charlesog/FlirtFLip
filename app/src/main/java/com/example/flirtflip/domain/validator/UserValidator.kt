package com.example.flirtflip.domain.validator

import com.example.flirtflip.domain.model.UserProfile

object UserValidator {
    /**
     * Explicación de la Regex de contraseña:
     *
     * ^                : Inicio de la cadena.
     * (?=.*[A-Z])      : Debe contener al menos una letra MAYÚSCULA.
     * (?=.*[a-z])      : Debe contener al menos una letra minúscula.
     * (?=.*\d)         : Debe contener al menos un dígito (número).
     * (?=.*[@$!%*?&])  : Debe contener al menos un carácter especial (@, $, !, %, *, ?, &).
     * [A-Za-z\d@$!%*?&]: Define el conjunto de caracteres permitidos.
     * {8,}             : Longitud mínima de 8 caracteres (sin límite máximo).
     * $                : Fin de la cadena.
     */
    private val passwordRegex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")

    fun validateOnlyAuth(email: String, passwordRaw: String): ValidationResult {
        return when {
            !email.contains("@") -> ValidationResult.Invalid("Invalid email format")
            !passwordRaw.contains(passwordRegex) -> ValidationResult.Invalid("The password does not meet the requirements")
            else -> ValidationResult.Valid
        }
    }

    fun validateFullProfile(userProfile: UserProfile): ValidationResult {
        return when {
            userProfile.mainPhotoUrl.isBlank() -> ValidationResult.Invalid("Main photo cannot be empty")
            userProfile.name.isBlank() -> ValidationResult.Invalid("Name cannot be empty")
            userProfile.age < 18 -> ValidationResult.Invalid("Age must be at least 18")
            userProfile.gender.isInvalid() -> ValidationResult.Invalid("Gender cannot be undefined")
            userProfile.galleryPhotoUrls.isEmpty() -> ValidationResult.Invalid("At least one photo is required")
            userProfile.galleryPhotoUrls.size > 6 -> ValidationResult.Invalid("Maximum of 6 photos allowed")
            else -> ValidationResult.Valid
        }
    }
}
