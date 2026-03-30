package com.example.flirtflip.domain.usecase

import com.example.flirtflip.domain.model.AppError
import com.example.flirtflip.domain.model.Swipe
import com.example.flirtflip.domain.model.SwipeType
import com.example.flirtflip.domain.repository.MatchRepository
import java.util.UUID
import javax.inject.Inject

class ProcessSwipeUseCase @Inject constructor(
    private val matchRepository: MatchRepository
){
    suspend operator fun invoke(fromUserId: String, toUserId: String, isLike: Boolean): Result<Boolean> {
        val swipe = Swipe(
            id = UUID.randomUUID().toString(),
            fromUserId = fromUserId,
            toUserId = toUserId,
            type = if (isLike) SwipeType.FLIRT else SwipeType.DISLIKE,
            timestamp = System.currentTimeMillis()
        )

        val saveResult = matchRepository.performSwipe(swipe)
        if (saveResult.isFailure) {
            val exception = saveResult.exceptionOrNull() ?: Exception("Unknown error")
            return Result.failure(AppError.UnknownError(exception as Exception))
        }

        if (isLike) {
            try {
                val exists = matchRepository.checkIfMatchExists(toUserId, fromUserId)
                if (exists) {
                    return Result.success(true)
                }
            } catch (e: Exception) {
                return Result.failure(AppError.UnknownError(e))
            }
        }
        return Result.success(false)
    }
}