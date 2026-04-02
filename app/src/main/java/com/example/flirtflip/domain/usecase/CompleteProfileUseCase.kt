package com.example.flirtflip.domain.usecase

import android.content.Context
import com.example.flirtflip.domain.model.AppError
import com.example.flirtflip.domain.model.User
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
    suspend operator fun invoke(profile: UserProfile, localPhotoUri: String? = null, context: Context) : Result<Unit> {
        val currentId = authRepository.currentUserId ?:
            return Result.failure(Exception("User not authenticated"))

        var finalProfile = profile

        if (!localPhotoUri.isNullOrBlank()) {
            val uploadResult = imageRepository.uploadUserImage(currentId, localPhotoUri, context)
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

        // Intentar actualizar el perfil
        val updateResult = userRepository.updateProfile(currentId, finalProfile)
        
        // Si falla porque el usuario no existe, crearlo
        return if (updateResult.isFailure && updateResult.exceptionOrNull() is AppError.UserNotFound) {
            // El usuario no existe, crearlo completo
            val newUser = User(
                id = currentId,
                email = "", // El email ya está en Auth, no es crítico aquí
                password = "", // No guardamos la contraseña
                profile = finalProfile
            )
            userRepository.saveUser(newUser)
        } else {
            updateResult
        }
    }
}