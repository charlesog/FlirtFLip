package com.example.flirtflip.data.repository

import com.example.flirtflip.domain.model.Match
import com.example.flirtflip.domain.model.Swipe
import com.example.flirtflip.domain.repository.MatchRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseMatchRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MatchRepository{

    override suspend fun performSwipe(swipe: Swipe): Result<Unit> {
        return try {
            firestore.collection("swipes")
                .document(swipe.id)
                .set(swipe)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkIfMatchExists(userId1: String, userId2: String): Boolean {
        val query = firestore.collection("swipes")
            .whereEqualTo("fromUserId", userId1)
            .whereEqualTo("toUserId", userId2)
            .whereEqualTo("type", "FLIRT")
            .get()
            .await()

        return !query.isEmpty
    }

    override suspend fun getMatchesForUser(userId: String): Flow<List<Match>> {
        TODO("Not yet implemented")
    }

    override fun getSwipesForUser(userId: String): Flow<List<Swipe>> {
        TODO("Not yet implemented")
    }


}