package com.example.flirtflip.domain.usecase

import com.example.flirtflip.domain.model.AppError
import com.example.flirtflip.domain.model.Gender
import com.example.flirtflip.domain.model.User
import com.example.flirtflip.domain.model.UserProfile
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
    suspend operator fun invoke(email: String, passwordRaw: String): Result<Unit> {
        val validation = UserValidator.validateOnlyAuth(email, passwordRaw)
        if (validation is ValidationResult.Invalid) {
            return Result.failure(AppError.ValidationError(validation.message))
        }

        val authResult = authRepository.signUp(email, passwordRaw)
        return authResult.fold(
            onSuccess = { uid ->
                val hashedPassword = securityProvider.hashPassword(passwordRaw)
                val finalUser = User(
                    id = uid,
                    email = email,
                    password = hashedPassword,
                    profile = UserProfile(
                        name = "",
                        firstSurname = "",
                        secondSurname = "",
                        age = 0,
                        gender = Gender.UNDEFINED,
                        bio = "",
                        hobbies = emptyList(),
                        mainPhotoUrl = "",
                        galleryPhotoUrls = emptyList()
                    )
                )

                userRepository.saveUser(finalUser)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}