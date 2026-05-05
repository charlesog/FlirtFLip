package com.example.flirtflip.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flirtflip.domain.model.Chat
import com.example.flirtflip.domain.repository.AuthRepository
import com.example.flirtflip.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUserId: String = authRepository.currentUserId ?: ""

    val chatSummaries: StateFlow<List<Chat>> = chatRepository.getMyChats(currentUserId)
        .onEach { chats ->
            Log.d("ChatListViewModel", "Chats cargados para $currentUserId: ${chats.size}")
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}