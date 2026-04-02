package com.example.flirtflip.domain.usecase

import android.content.Context
import android.util.Log
import com.example.flirtflip.domain.repository.AuthRepository
import com.example.flirtflip.domain.repository.ImageRepository
import javax.inject.Inject

class UploadGalleryImagesUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(localImageUris: List<String>, context: Context): Result<List<String>> {
        val userId = authRepository.currentUserId 
            ?: return Result.failure(Exception("User not authenticated"))

        val uploadedUrls = mutableListOf<String>()
        
        for ((index, uri) in localImageUris.withIndex()) {
            try {
                // Si ya es una URL de Firebase, mantenerla
                if (uri.startsWith("https://") || uri.startsWith("http://")) {
                    Log.d("UploadGalleryImages", "Image $index already uploaded: $uri")
                    uploadedUrls.add(uri)
                    continue
                }
                
                // Si no, subirla
                Log.d("UploadGalleryImages", "Uploading image $index: $uri")
                val result = imageRepository.uploadUserImage(userId, uri, context)
                result.fold(
                    onSuccess = { url -> 
                        Log.d("UploadGalleryImages", "Image $index uploaded successfully: $url")
                        uploadedUrls.add(url) 
                    },
                    onFailure = { error ->
                        Log.e("UploadGalleryImages", "Failed to upload image $index: ${error.message}", error)
                        return Result.failure(Exception("Error al subir imagen ${index + 1}: ${error.message}"))
                    }
                )
            } catch (e: Exception) {
                Log.e("UploadGalleryImages", "Unexpected error uploading image $index", e)
                return Result.failure(Exception("Error inesperado al subir imagen ${index + 1}: ${e.message}"))
            }
        }
        
        Log.d("UploadGalleryImages", "All images uploaded successfully. Total: ${uploadedUrls.size}")
        return Result.success(uploadedUrls)
    }
}
