package com.example.flirtflip.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flirtflip.domain.model.User
import com.example.flirtflip.domain.repository.AuthRepository
import com.example.flirtflip.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _currentUser.value = userRepository.getUser(userId)
            } catch (e: Exception) {
                // ignore
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onComplete()
        }
    }
}