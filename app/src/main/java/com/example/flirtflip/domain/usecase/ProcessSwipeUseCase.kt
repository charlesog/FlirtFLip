package com.example.flirtflip.domain.usecase

import com.example.flirtflip.domain.model.AppError
import com.example.flirtflip.domain.model.Match
import com.example.flirtflip.domain.model.Swipe
import com.example.flirtflip.domain.model.SwipeType
import com.example.flirtflip.domain.repository.MatchRepository
import java.util.UUID
import javax.inject.Inject

class ProcessSwipeUseCase @Inject constructor(
    private val matchRepository: MatchRepository
) {
    suspend operator fun invoke(fromUserId: String, toUserId: String, isLike: Boolean): Result<Boolean> {
        val swipe = Swipe(
            id = UUID.randomUUID().toString(),
            fromUserId = fromUserId,
            toUserId = toUserId,
            type = if (isLike) SwipeType.FLIRT else SwipeType.DISLIKE,
            timestamp = System.currentTimeMillis()
        )

        val saveResult = matchRepository.performSwipe(swipe)

        saveResult.onFailure {
            return Result.failure(AppError.UnknownError(it as Exception))
        }

        if (isLike) {
            return try {
                val exists = matchRepository.checkIfMatchExists(fromUserId, toUserId)

                if (exists) {
                    val newMatch = Match(
                        id = UUID.randomUUID().toString(),
                        idUser1 = fromUserId,
                        idUser2 = toUserId,
                        timestamp = System.currentTimeMillis()
                    )

                    matchRepository.saveMatch(newMatch)
                    Result.success(true)
                } else {
                    Result.success(false)
                }
            } catch (e: Exception) {
                Result.failure(AppError.UnknownError(e))
            }
        }

        return Result.success(false)
    }
}