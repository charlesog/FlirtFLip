package com.example.flirtflip.domain.usecase

import com.example.flirtflip.domain.model.AppError
import com.example.flirtflip.domain.model.User
import com.example.flirtflip.domain.repository.SecurityProvider
import com.example.flirtflip.domain.repository.UserRepository
import com.example.flirtflip.domain.validator.UserValidator
import com.example.flirtflip.domain.validator.ValidationResult
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val securityProvider: SecurityProvider
) {
    suspend operator fun invoke(user: User, passwordRaw: String): Result<Unit> {
        val validation = UserValidator.validateRegistration(user)
        if (validation is ValidationResult.Invalid) {
            return Result.failure(AppError.ValidationError(validation.message))
        }

        val userExists = try {
            userRepository.getUser(user.id) != null
        } catch (_: AppError.UserNotFound) {
            false
        } catch (_: Exception) {
            return Result.failure(AppError.NetworkError)
        }

        if (userExists) {
            return Result.failure(AppError.ValidationError("User already exists"))
        }

        return try {
            val hashedPassword = securityProvider.hashPassword(passwordRaw)
            userRepository.saveUser(user.copy(password = hashedPassword))
        } catch (e: Exception) {
            Result.failure(e as? AppError ?: AppError.UnknownError(e))
        }
    }
}