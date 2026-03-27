package com.example.flirtflip.domain.usecase

import com.example.flirtflip.domain.model.AppError
import com.example.flirtflip.domain.repository.ImageRepository
import com.example.flirtflip.domain.validator.UploadImageValidator
import com.example.flirtflip.domain.validator.ValidationResult
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
){
    suspend operator fun invoke(userId: String, imageUriStr: String): Result<String> {
        val validation = UploadImageValidator.validateImage(imageUriStr)
        if (validation is ValidationResult.Invalid) {
            return Result.failure(AppError.ValidationError(validation.message))
        }

        return imageRepository.uploadUserImage(userId, imageUriStr)
    }
}