package com.example.flirtflip.domain.model

sealed class AppError : Exception() {
    class NetworkError : AppError()
    class LocationDisabled : AppError()
    class UserNotFound : AppError()

    data class UnknownError(val error: Exception) : AppError()
    data class ValidationError(override val message: String) : AppError()
    data class AuthenticationError(override val message: String) : AppError()
}