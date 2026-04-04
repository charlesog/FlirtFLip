package com.example.flirtflip.domain.model

sealed class SwipeUiState {
    object Loading : SwipeUiState()
    data class Success(
        val users: List<User>,
        val currentIndex: Int = 0
    ) : SwipeUiState()
    data class Error(val message: String) : SwipeUiState()
    object Empty : SwipeUiState()
}