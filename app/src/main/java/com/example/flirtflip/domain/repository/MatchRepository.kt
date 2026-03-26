package com.example.flirtflip.domain.repository

import com.example.flirtflip.domain.model.Match
import com.example.flirtflip.domain.model.Swipe
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    suspend fun performSwipe(swipe: Swipe): Result<Unit>
    suspend fun getMatchesForUser(userId: String): Flow<List<Match>>
    suspend fun checkIfMatchExists(userId1: String, userId2: String): Boolean
}