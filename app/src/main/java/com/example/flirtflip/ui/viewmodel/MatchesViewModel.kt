package com.example.flirtflip.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flirtflip.domain.model.Message
import com.example.flirtflip.domain.model.MessageType
import com.example.flirtflip.domain.model.User
import com.example.flirtflip.domain.repository.AuthRepository
import com.example.flirtflip.domain.repository.ChatRepository
import com.example.flirtflip.domain.repository.MatchRepository
import com.example.flirtflip.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _matchedUsers = MutableStateFlow<List<User>>(emptyList())
    val matchedUsers = _matchedUsers.asStateFlow()

    private val _likesReceivedAges = MutableStateFlow<List<Int>>(emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    val currentUserId: String get() = authRepository.currentUserId ?: ""

    init {
        loadMatches()
    }

    private fun loadMatches() {
        val myId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            matchRepository.getMatchesForUser(myId).collect { matches ->
                val users = matches.mapNotNull { match ->
                    val otherId = if (match.idUser1 == myId) match.idUser2 else match.idUser1
                    userRepository.getUser(otherId)
                }
                _matchedUsers.value = users
                _isLoading.value = false
            }
        }
    }

    private fun loadLikesReceived() {
        val myId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            matchRepository.getSwipesForUser(myId).collect { swipes ->
                val matchedIds = _matchedUsers.value.map { it.id }.toSet()
                val pendingLikes = swipes.filter { it.fromUserId !in matchedIds }
                val ages = pendingLikes.mapNotNull { swipe ->
                    userRepository.getUser(swipe.fromUserId)?.profile?.age
                }
                _likesReceivedAges.value = ages
            }
        }
    }

    fun sendHello(otherUserId: String, onDone: (chatId: String) -> Unit) {
        val myId = authRepository.currentUserId ?: return
        val chatId = listOf(myId, otherUserId).sorted().joinToString("_")
        val mensaje = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = myId,
            text = "¡Hola! 👋",
            timestamp = LocalDateTime.now(),
            liked = false,
            isRead = false,
            messageType = MessageType.TEXT
        )
        viewModelScope.launch {
            chatRepository.sendMessage(chatId, mensaje)
            onDone(chatId)
        }
    }
}