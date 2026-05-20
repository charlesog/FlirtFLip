package com.example.flirtflip.data.repository

import com.example.flirtflip.data.entity.MatchEntity
import com.example.flirtflip.data.mapper.toDomain
import com.example.flirtflip.data.mapper.toEntity
import com.example.flirtflip.domain.model.Match
import com.example.flirtflip.domain.model.Swipe
import com.example.flirtflip.domain.model.SwipeType
import com.example.flirtflip.domain.repository.MatchRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseMatchRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MatchRepository {

    override suspend fun performSwipe(swipe: Swipe): Result<Unit> {
        return try {
            val entity = swipe.toEntity()

            firestore.collection("swipes")
                .document(entity.id)
                .set(entity)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkIfMatchExists(userId1: String, userId2: String): Boolean {
        val query = firestore.collection("swipes")
            .whereEqualTo("fromUserId", userId2)
            .whereEqualTo("toUserId", userId1)
            .whereEqualTo("type", SwipeType.FLIRT.name)
            .get()
            .await()

        return !query.isEmpty
    }

    override suspend fun getMatchesForUser(userId: String): Flow<List<Match>> = callbackFlow {
        val subscription = firestore.collection("matches")
            .whereArrayContains("users", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val matches = snapshot?.toObjects(MatchEntity::class.java)
                    ?.map { it.toDomain() } ?: emptyList()

                trySend(matches)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun saveMatch(match: Match): Result<Unit> {
        return try {
            val entity = match.toEntity()
            firestore.collection("matches")
                .document(entity.id)
                .set(entity)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSwipesForUser(userId: String): Flow<List<Swipe>> = callbackFlow {
        val subscription = firestore.collection("swipes")
            .whereEqualTo("toUserId", userId)
            .whereEqualTo("type", SwipeType.FLIRT.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val swipes = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Swipe(
                            id = doc.id,
                            fromUserId = doc.getString("fromUserId") ?: "",
                            toUserId = doc.getString("toUserId") ?: "",
                            type = SwipeType.valueOf(doc.getString("type") ?: "PASS"),
                            timestamp = doc.getLong("timestamp") ?: 0L
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(swipes)
            }
        awaitClose { subscription.remove() }
    }
}