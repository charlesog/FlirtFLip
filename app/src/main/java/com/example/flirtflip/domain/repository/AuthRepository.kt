package com.example.flirtflip.domain.repository

interface AuthRepository {
    val currentUserId: String?

    suspend fun signUp(email: String, password: String) : Result<String>
    suspend fun login(email: String, password: String) : Result<String>
    suspend fun signOut()
}