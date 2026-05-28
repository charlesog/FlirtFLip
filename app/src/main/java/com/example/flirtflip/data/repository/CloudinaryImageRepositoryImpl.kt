package com.example.flirtflip.data.repository

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.example.flirtflip.BuildConfig
import com.example.flirtflip.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

class CloudinaryImageRepositoryImpl @Inject constructor(
    private val okHttpClient: OkHttpClient
) : ImageRepository {

    companion object {
        private const val CLOUD_NAME = BuildConfig.CLOUDINARY_CLOUD_NAME
        private const val UPLOAD_PRESET = BuildConfig.CLOUDINARY_UPLOAD_PRESET
        private val UPLOAD_URL get() = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"
    }

    override suspend fun uploadUserImage(
        userId: String,
        imageUriStr: String,
        context: Context
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("CloudinaryRepo", "Starting upload for URI: $imageUriStr")

            val uri = imageUriStr.toUri()
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("No se pudo abrir la imagen"))

            val imageBytes = inputStream.readBytes()
            inputStream.close()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    "img_${System.currentTimeMillis()}.jpg",
                    imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
                )
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .addFormDataPart("folder", "users/$userId")
                .build()

            val request = Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()
                ?: return@withContext Result.failure(Exception("Respuesta vacía de Cloudinary"))

            if (!response.isSuccessful) {
                Log.e("CloudinaryRepo", "Upload failed: $responseBody")
                return@withContext Result.failure(Exception("Error al subir imagen: ${response.code}"))
            }

            val json = JSONObject(responseBody)
            val secureUrl = json.getString("secure_url")
            Log.d("CloudinaryRepo", "Upload successful: $secureUrl")

            Result.success(secureUrl)

        } catch (e: Exception) {
            Log.e("CloudinaryRepo", "Upload failed for URI: $imageUriStr", e)
            Result.failure(Exception("Error al subir imagen: ${e.message}", e))
        }
    }
}