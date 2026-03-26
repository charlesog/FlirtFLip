package com.example.flirtflip.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flirtflip.domain.model.Gender
import com.example.flirtflip.domain.model.User
import com.example.flirtflip.domain.model.UserProfile
import com.example.flirtflip.domain.usecase.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun signUp(
        name: String,
        firstSurname: String,
        secondSurname: String,
        email: String,
        password: String,
        confirmPassword: String,
        acceptedTerms: Boolean,
        acceptedAge: Boolean
    ) {
        // Validaciones básicas
        if (!acceptedTerms || !acceptedAge) {
            _uiState.value = SignUpUiState.Error("Debes aceptar los términos y condiciones")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = SignUpUiState.Error("Las contraseñas no coinciden")
            return
        }

        if (name.isBlank() || firstSurname.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = SignUpUiState.Error("Todos los campos son obligatorios")
            return
        }

        _uiState.value = SignUpUiState.Loading

        viewModelScope.launch {
            try {
                // Crear el perfil temporal del usuario (valores por defecto)
                val userProfile = UserProfile(
                    name = name,
                    firstSurname = firstSurname,
                    secondSurname = secondSurname,
                    age = 18, // Edad por defecto, se completará en el siguiente paso
                    gender = Gender.UNDEFINED,
                    bio = "",
                    hobbies = emptyList(),
                    imageUrl = emptyList(),
                    job = null,
                    location = null
                )

                val user = User(
                    id = "", // Se asignará por Firebase
                    email = email.trim(),
                    password = password, // Se hasheará en el UseCase
                    profile = userProfile
                )

                val result = registerUserUseCase(user, password)

                result.fold(
                    onSuccess = {
                        _uiState.value = SignUpUiState.Success
                    },
                    onFailure = { error ->
                        _uiState.value = SignUpUiState.Error(
                            error.message ?: "Error desconocido al registrar usuario"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = SignUpUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun resetState() {
        _uiState.value = SignUpUiState.Idle
    }
}

sealed class SignUpUiState {
    object Idle : SignUpUiState()
    object Loading : SignUpUiState()
    object Success : SignUpUiState()
    data class Error(val message: String) : SignUpUiState()
}

