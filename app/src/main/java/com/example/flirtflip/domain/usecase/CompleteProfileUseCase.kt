package com.example.flirtflip.domain.usecase

import com.example.flirtflip.domain.model.AppError
import com.example.flirtflip.domain.model.UserProfile
import com.example.flirtflip.domain.repository.AuthRepository
import com.example.flirtflip.domain.repository.UserRepository
import com.example.flirtflip.domain.validator.UserValidator
import com.example.flirtflip.domain.validator.ValidationResult
import javax.inject.Inject

class CompleteProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(updateProfile: UserProfile) : Result<Unit> {
        val currentId = authRepository.getCurrentUserId() ?:
            return Result.failure(Exception("User not authenticated"))


        val validation = UserValidator.validateFullProfile(updateProfile)
        if (validation is ValidationResult.Invalid) {
            return Result.failure(AppError.ValidationError(validation.message))
        }

        return userRepository.updateProfile(currentId, updateProfile)
    }
}