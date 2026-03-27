package com.example.flirtflip.data.repository

import com.example.flirtflip.domain.repository.ImageRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import androidx.core.net.toUri

class FirebaseImageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
) : ImageRepository {

    override suspend fun uploadUserImage(userId: String, imageUriStr: String): Result<String> {
        return try {
            val filename = "img_${System.currentTimeMillis()}.jpg"
            val imageRef = storage.reference.child("users/$userId/images/$filename")

            val uri = imageUriStr.toUri()
            imageRef.putFile(uri).await()

            val downloadUrl = imageRef.downloadUrl.await().toString()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}