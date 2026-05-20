package com.example.flirtflip.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flirtflip.domain.model.Hobby
import com.example.flirtflip.domain.model.User
import com.example.flirtflip.domain.repository.AuthRepository
import com.example.flirtflip.domain.repository.ChatRepository
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
    private val processSwipeUseCase: ProcessSwipeUseCase,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SwipeUiState>(SwipeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val currentUserId: String? get() = authRepository.currentUserId

    private val _myHobbies = MutableStateFlow<List<Hobby>>(emptyList())
    val myHobbies = _myHobbies.asStateFlow()

    // Guarda todos los perfiles para poder re-filtrar sin ir a Firebase
    private var _allProfiles: List<User> = emptyList()

    // Rango de edad persistente
    private val _ageRange = MutableStateFlow(18f..80f)
    val ageRange = _ageRange.asStateFlow()

    init {
        loadUsers()
    }

    fun applyAgeFilter(range: ClosedFloatingPointRange<Float>) {
        _ageRange.value = range
        val minAge = range.start.toInt()
        val maxAge = range.endInclusive.toInt()
        val filtered = _allProfiles.filter { user ->
            user.profile.age in minAge..maxAge
        }
        _uiState.value = if (filtered.isEmpty()) SwipeUiState.Empty
        else SwipeUiState.Success(profiles = filtered)
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = SwipeUiState.Loading
            val myId = currentUserId ?: run {
                _uiState.value = SwipeUiState.Empty
                return@launch
            }

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

                // Guardar para re-filtrar
                _allProfiles = finalProfiles

                // Aplicar filtro de edad actual
                val minAge = _ageRange.value.start.toInt()
                val maxAge = _ageRange.value.endInclusive.toInt()
                val ageFiltered = finalProfiles.filter { user ->
                    user.profile.age in minAge..maxAge
                }

                _uiState.value = if (ageFiltered.isEmpty()) SwipeUiState.Empty
                else SwipeUiState.Success(profiles = ageFiltered)
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
            currentState.matchedUser?.let { matchedUser ->
                val myId = authRepository.currentUserId ?: ""
                val chatId = listOf(myId, matchedUser.id).sorted().joinToString("_")
                viewModelScope.launch {
                    chatRepository.ensureChatExists(chatId, listOf(myId, matchedUser.id).sorted())
                }
            }

            if (currentState.profiles.isEmpty()) {
                _uiState.value = SwipeUiState.Empty
            } else {
                _uiState.value = currentState.copy(showMatchDialog = false, matchedUser = null)
            }
        }
    }
}