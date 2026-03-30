package com.example.flirtflip.data.repository

import com.example.flirtflip.domain.model.AppError
import com.example.flirtflip.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    override suspend fun signUp(email: String, password: String): Result<String> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("UID nulo")

            Result.success(uid)
        } catch (_: Exception) {
            Result.failure(AppError.NetworkError())
        }
    }

    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val uid= result.user?.uid ?:throw Exception("UID nulo")
            Result.success(uid)
        } catch (_: Exception) {
            Result.failure(AppError.NetworkError())
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}
