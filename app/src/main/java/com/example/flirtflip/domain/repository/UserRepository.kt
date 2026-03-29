package com.example.flirtflip.domain.repository

import com.example.flirtflip.domain.model.User
import com.example.flirtflip.domain.model.UserProfile

interface UserRepository {
    suspend fun getUser(id: String): User?
    suspend fun saveUser(user: User): Result<Unit>
    suspend fun getAllUsers(): List<User>
    suspend fun updateProfile(userId: String, profile: UserProfile): Result<Unit>
    suspend fun deleteUser(id: String): Result<Unit>

}