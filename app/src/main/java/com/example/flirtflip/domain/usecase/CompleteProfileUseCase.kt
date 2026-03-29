package com.example.flirtflip.domain.usecase

import com.example.flirtflip.domain.model.AppError
import com.example.flirtflip.domain.model.UserProfile
import com.example.flirtflip.domain.repository.AuthRepository
import com.example.flirtflip.domain.repository.ImageRepository
import com.example.flirtflip.domain.repository.UserRepository
import com.example.flirtflip.domain.validator.UserValidator
import com.example.flirtflip.domain.validator.ValidationResult
import javax.inject.Inject

class CompleteProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(profile: UserProfile, localPhotoUri: String? = null) : Result<Unit> {
        val currentId = authRepository.getCurrentUserId() ?:
            return Result.failure(Exception("User not authenticated"))

        var finalProfile = profile

        if (!localPhotoUri.isNullOrBlank()) {
            val uploadResult = imageRepository.uploadUserImage(currentId, localPhotoUri)
            if (uploadResult.isSuccess) {
                val cloudUrl = uploadResult.getOrThrow()
                finalProfile = finalProfile.copy(mainPhotoUrl = cloudUrl)
            } else {
                return Result.failure(AppError.NetworkError())
            }
        }

        val validation = UserValidator.validateFullProfile(finalProfile)
        if (validation is ValidationResult.Invalid) {
            return Result.failure(AppError.ValidationError(validation.message))
        }

        return userRepository.updateProfile(currentId, finalProfile)
    }
}