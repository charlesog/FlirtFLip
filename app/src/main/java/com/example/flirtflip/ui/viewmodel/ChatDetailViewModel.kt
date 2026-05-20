package com.example.flirtflip.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flirtflip.domain.model.Message
import com.example.flirtflip.domain.repository.AuthRepository
import com.example.flirtflip.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val chatId: String = savedStateHandle.get<String>("chatId") ?: ""
    val currentUserId = authRepository.currentUserId ?: ""

    val messages: StateFlow<List<Message>> = chatRepository.getMessages(chatId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun sendTextMessage(text: String) {
        if (text.isBlank()) return

        val newMessage = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = currentUserId,
            text = text,
            timestamp = LocalDateTime.now(),
            liked = false,
            isRead = false
        )

        viewModelScope.launch {
            chatRepository.sendMessage(chatId, newMessage)
        }
    }

    /**
     * Envía una propuesta de Date Roulette como mensaje especial.
     * El texto incluye un prefijo especial "🎲DATE_ROULETTE:" para que la UI
     * pueda renderizarlo de forma diferente al resto de mensajes.
     */
    fun sendDateRouletteMessage(emoji: String, title: String, description: String) {
        val rouletteText = "🎲DATE_ROULETTE:$emoji|$title|$description"
        val newMessage = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = currentUserId,
            text = rouletteText,
            timestamp = LocalDateTime.now(),
            liked = false,
            isRead = false
        )
        viewModelScope.launch {
            chatRepository.sendMessage(chatId, newMessage)
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            chatRepository.deleteMessage(chatId, messageId)
        }
    }
}