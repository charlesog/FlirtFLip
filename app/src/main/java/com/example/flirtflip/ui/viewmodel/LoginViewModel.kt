package com.example.flirtflip.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flirtflip.data.preferences.UserPreferences
import com.example.flirtflip.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    var email        by mutableStateOf("")
    var password     by mutableStateOf("")
    var rememberMe   by mutableStateOf(false)
    var isLoading    by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            val savedRemember = userPreferences.rememberMe.first()
            val savedEmail    = userPreferences.savedEmail.first()
            rememberMe = savedRemember
            if (savedRemember && savedEmail.isNotEmpty()) {
                email = savedEmail
            }
        }
    }

    /**
     * onSuccess recibe un booleano: true = ir al WelcomeBot, false = ir a Home
     */
    fun onLoginClick(onSuccess: (showWelcome: Boolean) -> Unit) {
        viewModelScope.launch {
            isLoading    = true
            errorMessage = null

            val result = authRepository.login(email, password)

            result.fold(
                onSuccess = { uid ->
                    userPreferences.saveRememberMe(rememberMe, email)

                    // Comprobamos si este usuario ya vio el bot de bienvenida
                    val alreadySeen = userPreferences.hasSeenWelcome(uid)
                    onSuccess(!alreadySeen)   // true = mostrar bot, false = saltar
                },
                onFailure = {
                    errorMessage = it.message ?: "Error al iniciar sesión"
                }
            )
            isLoading = false
        }
    }
}