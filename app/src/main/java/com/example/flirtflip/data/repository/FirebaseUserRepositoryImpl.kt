package com.example.flirtflip.data.repository

import com.example.flirtflip.data.entity.UserEntity
import com.example.flirtflip.data.mapper.toDomain
import com.example.flirtflip.data.mapper.toEntity
import com.example.flirtflip.domain.model.AppError
import com.example.flirtflip.domain.model.Location
import com.example.flirtflip.domain.model.User
import com.example.flirtflip.domain.model.UserProfile
import com.example.flirtflip.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    private val userCollection = firestore.collection("users")

    override suspend fun getUser(id: String): User? {
        return try {
            val document = userCollection.document(id).get().await()
            if (document.exists()) {
                val entity = document.toObject(UserEntity::class.java)
                entity?.toDomain()
            } else
                null
        } catch (e: Exception) {
            throw AppError.UnknownError(e)
        }
    }

    override suspend fun saveUser(user: User): Result<Unit> {
        return try {
            val userEntity = user.toEntity()

            userCollection.document(userEntity.id).set(userEntity).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(AppError.NetworkError())
        }
    }

    override suspend fun getAllUsers(): Result<List<User>> {
        try {
            val snapshot = userCollection.get().await()
            val entities = snapshot.toObjects(UserEntity::class.java)

            val domainUsers = entities.map { it.toDomain() }
            return Result.success(domainUsers)
        } catch (e: Exception) {
            return Result.failure(AppError.UnknownError(e))
        }
    }

    override suspend fun updateProfile(userId: String, profile: UserProfile): Result<Unit> {
        return try {
            val profileEntity = profile.toEntity()

            userCollection.document(userId)
                .update("profile", profileEntity)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            val error = when {
                e is FirebaseFirestoreException && e.code ==
                        FirebaseFirestoreException.Code.NOT_FOUND -> AppError.UserNotFound()

                else -> AppError.NetworkError()
            }
            Result.failure(error)
        }
    }

    override suspend fun deleteUser(id: String): Result<Unit> {
        return try {
            userCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (_: Exception) {
            Result.failure(AppError.NetworkError())
        }
    }

    override suspend fun updateLocation(userId: String, location: Location): Result<Unit> {
        try {
            userCollection.document(userId)
                .update("location", location)
                .await()

            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(AppError.NetworkError())
        }
    }

    override suspend fun getInteractedUserIds(userId: String): Result<List<String>> {
        return try {
            val snapshot = firestore.collection("swipes")
                .whereEqualTo("fromUserId", userId)
                .get()
                .await()

            val ids = snapshot.documents.mapNotNull { it.getString("toUserId") }
            Result.success(ids)
        } catch (_: Exception) {
            Result.failure(AppError.NetworkError())
        }
    }
}