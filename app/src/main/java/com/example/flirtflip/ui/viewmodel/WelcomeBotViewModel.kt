package com.example.flirtflip.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flirtflip.data.preferences.UserPreferences
import com.example.flirtflip.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeBotViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val authRepository: AuthRepository
) : ViewModel() {

    fun markWelcomeSeen() {
        val uid = authRepository.currentUserId ?: return
        viewModelScope.launch {
            userPreferences.markWelcomeSeen(uid)
        }
    }
}