package com.example.flirtflip.domain.model

sealed class AppError : Exception() {
    object NetworkError : AppError()
    object LocationDisabled : AppError()
    object PermissionDenied : AppError()
    object UserNotFound : AppError()

    data class UnknownError(val error: Exception) : AppError()
    data class ValidationError(override val message: String) : AppError()
    data class AuthenticationError(override val message: String) : AppError()
}