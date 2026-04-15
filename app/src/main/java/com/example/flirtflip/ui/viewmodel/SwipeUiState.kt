package com.example.flirtflip.ui.viewmodel

import com.example.flirtflip.domain.model.User

sealed class SwipeUiState {
    data object Loading : SwipeUiState()
    data object Empty : SwipeUiState()

    data class Success(
        val profiles: List<User>,
        val currentIndex: Int = 0,
        val showMatchDialog: Boolean = false,
        val matchedUser: User? = null
    ) : SwipeUiState()

    data class Error(val message: String) : SwipeUiState()
}