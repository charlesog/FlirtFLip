package com.example.flirtflip.domain.validator

object UploadImageValidator {
    private val ALLOWED_FILE_TYPES = listOf("jpg", "jpeg", "png")

    private fun isValidExtension(uriStr: String): Boolean {
        val extension = uriStr.substringAfterLast('.', "").lowercase()
        return if (extension.isEmpty()) true else ALLOWED_FILE_TYPES.contains(extension)
    }

    fun validateImage(uriStr: String): ValidationResult{
        return when {
            uriStr.isBlank() -> ValidationResult.Invalid("Image cannot be empty")

            !isValidExtension(uriStr) -> ValidationResult.Invalid("Invalid file type")
            else -> ValidationResult.Valid
        }
    }
}