package com.example.flirtflip.data.repository

import com.example.flirtflip.domain.model.AppError
import com.example.flirtflip.domain.model.User
import com.example.flirtflip.domain.model.UserProfile
import com.example.flirtflip.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore : FirebaseFirestore
) : UserRepository{

    private val userCollection = firestore.collection("users")

    override suspend fun getUser(id: String): User? {
        return try {
            val document = userCollection.document(id).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            throw AppError.UnknownError(e)
        }
    }

    override suspend fun saveUser(user: User): Result<Unit> {
        return try {
            userCollection.document(user.id).set(user).await()
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppError.NetworkError)
        }
    }

    override suspend fun getAllUsers(): List<User> {
        return try {
            val snapshot = userCollection.get().await()
            snapshot.toObjects(User::class.java).filterNotNull()
        } catch (_ : Exception) {
            emptyList()
        }
    }

    override suspend fun updateProfile(userId: String, profile: UserProfile): Result<Unit> {
        return try {
            userCollection.document(userId)
                .update("profile", profile)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            val error = when{
                e is FirebaseFirestoreException && e.code ==
                        FirebaseFirestoreException.Code.NOT_FOUND -> AppError.UserNotFound
                else -> AppError.NetworkError
            }
            Result.failure(error)
        }
    }
}