package com.example.flirtflip.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flirtflip.domain.model.User
import com.example.flirtflip.domain.repository.AuthRepository
import com.example.flirtflip.domain.repository.UserRepository
import com.example.flirtflip.domain.usecase.ProcessSwipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SwipeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val processSwipeUseCase: ProcessSwipeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SwipeUiState>(SwipeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = SwipeUiState.Loading
            userRepository.getAllUsers().fold(
                onSuccess = { users ->
                    val filteredUsers = users.filter { it.id != authRepository.currentUserId }
                    if (filteredUsers.isEmpty()) {
                        _uiState.value = SwipeUiState.Empty
                    } else {
                        _uiState.value = SwipeUiState.Success(profiles = filteredUsers)
                    }
                },
                onFailure = { _uiState.value = SwipeUiState.Error("Error al cargar perfiles") }
            )
        }
    }

    fun onSwipe(targetUser: User, isLike: Boolean) {
        val fromId = authRepository.currentUserId ?: return

        viewModelScope.launch {
            val result = processSwipeUseCase(
                fromUserId = fromId,
                toUserId = targetUser.id,
                isLike = isLike
            )

            val currentState = _uiState.value
            if (currentState is SwipeUiState.Success) {
                val isMatch = result.getOrDefault(false)
                val nextProfiles = currentState.profiles.filter { it.id != targetUser.id }

                if (nextProfiles.isEmpty()) {
                    if (isMatch) {
                        _uiState.value = currentState.copy(
                            profiles = emptyList(),
                            showMatchDialog = true,
                            matchedUser = targetUser
                        )
                    } else {
                        _uiState.value = SwipeUiState.Empty
                    }
                } else {
                    _uiState.value = currentState.copy(
                        profiles = nextProfiles,
                        showMatchDialog = isMatch,
                        matchedUser = if (isMatch) targetUser else null,
                        currentIndex = 0
                    )
                }
            }
        }
    }

    fun dismissMatchDialog() {
        val currentState = _uiState.value
        if (currentState is SwipeUiState.Success) {
            if (currentState.profiles.isEmpty()) {
                _uiState.value = SwipeUiState.Empty
            } else {
                _uiState.value = currentState.copy(showMatchDialog = false, matchedUser = null)
            }
        }
    }
}