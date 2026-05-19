package com.example.flirtflip.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flirtflip.domain.model.User
import com.example.flirtflip.domain.repository.AuthRepository
import com.example.flirtflip.domain.repository.MatchRepository
import com.example.flirtflip.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _matchedUsers = MutableStateFlow<List<User>>(emptyList())
    val matchedUsers = _matchedUsers.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

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
}