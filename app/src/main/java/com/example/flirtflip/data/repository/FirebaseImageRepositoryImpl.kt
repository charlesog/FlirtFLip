package com.example.flirtflip.data.repository

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.example.flirtflip.domain.repository.ImageRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseImageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
) : ImageRepository {

    override suspend fun uploadUserImage(userId: String, imageUriStr: String, context: Context): Result<String> {
        return try {
            Log.d("FirebaseImageRepo", "Starting upload for URI: $imageUriStr")
            
            val filename = "img_${System.currentTimeMillis()}.jpg"
            val imageRef = storage.reference.child("users/$userId/images/$filename")

            val uri = imageUriStr.toUri()
            Log.d("FirebaseImageRepo", "Parsed URI: $uri, Path: ${imageRef.path}")
            
            // Usar InputStream en lugar de putFile para manejar content:// URIs
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return Result.failure(Exception("No se pudo abrir la imagen"))
            
            val uploadTask = imageRef.putStream(inputStream).await()
            inputStream.close()
            
            Log.d("FirebaseImageRepo", "Upload completed, bytes transferred: ${uploadTask.bytesTransferred}")

            val downloadUrl = imageRef.downloadUrl.await().toString()
            Log.d("FirebaseImageRepo", "Download URL obtained: $downloadUrl")

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e("FirebaseImageRepo", "Upload failed for URI: $imageUriStr", e)
            Result.failure(Exception("Error al subir imagen: ${e.message}", e))
        }
    }
}