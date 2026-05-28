package com.example.flirtflip.domain.repository

import android.content.Context

interface ImageRepository {
    suspend fun uploadUserImage(
        userId: String,
        imageUriStr: String,
        context: Context
    ): Result<String>
}