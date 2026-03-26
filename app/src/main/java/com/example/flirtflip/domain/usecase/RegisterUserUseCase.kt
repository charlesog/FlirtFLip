package com.example.flirtflip.domain.usecase

import com.example.flirtflip.domain.model.AppError
import com.example.flirtflip.domain.model.User
import com.example.flirtflip.domain.repository.AuthRepository
import com.example.flirtflip.domain.repository.SecurityProvider
import com.example.flirtflip.domain.repository.UserRepository
import com.example.flirtflip.domain.validator.UserValidator
import com.example.flirtflip.domain.validator.ValidationResult
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val securityProvider: SecurityProvider,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: User, passwordRaw: String): Result<Unit> {
        val validation = UserValidator.validateRegistration(user)
        if (validation is ValidationResult.Invalid) {
            return Result.failure(AppError.ValidationError(validation.message))
        }

        val authResult = authRepository.signUp(user.email, passwordRaw)
        return authResult.fold(
            onSuccess = { uid ->
                val hashedPassword = securityProvider.hashPassword(passwordRaw)
                val finalUser = user.copy(
                    id = uid,
                    password = hashedPassword
                )

                userRepository.saveUser(finalUser)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}