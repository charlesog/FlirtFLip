package com.example.flirtflip.domain.usecase

import com.example.flirtflip.domain.repository.AuthRepository
import com.example.flirtflip.domain.repository.ChatRepository
import javax.inject.Inject

class GetOrCreateChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(otherUserId: String): String {
        val myId = authRepository.currentUserId ?: return ""
        val chatId = listOf(myId, otherUserId).sorted().joinToString("_")

        return chatId
    }
}