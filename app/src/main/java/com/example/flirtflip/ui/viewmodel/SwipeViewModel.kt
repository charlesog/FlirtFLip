package com.example.flirtflip.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flirtflip.domain.model.Hobby
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

    val currentUserId: String? get() = authRepository.currentUserId

    private val _myHobbies = MutableStateFlow<List<Hobby>>(emptyList())
    val myHobbies = _myHobbies.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = SwipeUiState.Loading
            val myId = currentUserId ?: return@launch

            // Cargar hobbies del usuario actual
            userRepository.getUser(myId)?.let { me ->
                _myHobbies.value = me.profile.hobbies
            }

            val allUserResult = userRepository.getAllUsers()
            val interactedUserIdsResult = userRepository.getInteractedUserIds(myId)

            if (allUserResult.isSuccess && interactedUserIdsResult.isSuccess) {
                val allUsers = allUserResult.getOrThrow()
                val seenIds = interactedUserIdsResult.getOrThrow()

                val finalProfiles = allUsers.filter { user ->
                    user.id != myId && !seenIds.contains(user.id)
                }

                if (finalProfiles.isEmpty()) {
                    _uiState.value = SwipeUiState.Empty
                } else {
                    _uiState.value = SwipeUiState.Success(profiles = finalProfiles)
                }
            } else {
                _uiState.value = SwipeUiState.Error("Fallo al sincronizar datos")
            }
        }
    }

    fun onSwipe(targetUser: User, isLike: Boolean) {
        val fromId = currentUserId ?: return

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
                    _uiState.value = if (isMatch) {
                        currentState.copy(
                            profiles = emptyList(),
                            showMatchDialog = true,
                            matchedUser = targetUser
                        )
                    } else {
                        SwipeUiState.Empty
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