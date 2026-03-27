package com.example.flirtflip.domain.repository

interface ImageRepository {
    suspend fun uploadUserImage(userId: String, imageUriStr: String): Result<String>
}