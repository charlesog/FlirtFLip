package com.example.flirtflip.domain.repository

import com.example.flirtflip.domain.model.Chat
import com.example.flirtflip.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(chatId: String): Flow<List<Message>>
    suspend fun sendMessage(chatId: String, message: Message): Result<Unit>
    fun getMyChats(userId: String): Flow<List<Chat>>
    suspend fun deleteMessage(chatId: String, messageId: String): Result<Unit>
    suspend fun ensureChatExists(chatId: String, userIds: List<String>): Result<Unit>

}