package com.example.flirtflip.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flirtflip.domain.model.Gender
import com.example.flirtflip.domain.model.Hobby
import com.example.flirtflip.domain.model.UserProfile
import com.example.flirtflip.domain.repository.AuthRepository
import com.example.flirtflip.domain.repository.UserRepository
import com.example.flirtflip.domain.usecase.CompleteProfileUseCase
import com.example.flirtflip.domain.usecase.UploadGalleryImagesUseCase
import com.example.flirtflip.domain.usecase.UploadImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val completeProfileUseCase: CompleteProfileUseCase,
    @Suppress("unused") private val uploadImageUseCase: UploadImageUseCase,
    private val uploadGalleryImagesUseCase: UploadGalleryImagesUseCase,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var name by mutableStateOf("")
    var firstSurname by mutableStateOf("")
    var secondSurname by mutableStateOf("")
    var age by mutableStateOf("")
    var gender by mutableStateOf(Gender.UNDEFINED)
    var bio by mutableStateOf("")
    var job by mutableStateOf("")
    var selectedHobbies by mutableStateOf<List<Hobby>>(emptyList())
    var mainPhotoUri by mutableStateOf<String?>(null)
    var galleryPhotoUris by mutableStateOf<List<String>>(emptyList())

    var isCreatingProfile by mutableStateOf(true)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    init {
        loadCurrentProfile()
    }

    private fun loadCurrentProfile() {
        viewModelScope.launch {
            isLoading = true
            try {
                val userId = authRepository.currentUserId
                if (userId != null) {
                    val user = userRepository.getUser(userId)
                    if (user != null) {
                        // Usuario existe, cargar su perfil
                        val profile = user.profile
                        name = profile.name
                        firstSurname = profile.firstSurname
                        secondSurname = profile.secondSurname
                        age = if (profile.age > 0) profile.age.toString() else ""
                        gender = profile.gender
                        bio = profile.bio
                        job = profile.job ?: ""
                        selectedHobbies = profile.hobbies
                        mainPhotoUri = profile.mainPhotoUrl.ifBlank { null }
                        galleryPhotoUris = profile.galleryPhotoUrls
                        isCreatingProfile = false
                    }
                    // Si el usuario no existe aún, dejamos los campos vacíos (valores por defecto)
                    // Esto es normal para usuarios nuevos que aún no han completado su perfil
                }
            } catch (e: Exception) {
                // No mostramos error si el perfil no existe, es normal para nuevos usuarios
                // Solo mostramos errores reales de red o permisos
                if (e.message?.contains("NOT_FOUND") == false) {
                    errorMessage = "Error al cargar el perfil: ${e.message}"
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun onNameChanged(newName: String) {
        name = newName
        errorMessage = null
    }

    fun onFirstSurnameChanged(newSurname: String) {
        firstSurname = newSurname
        errorMessage = null
    }

    fun onSecondSurnameChanged(newSurname: String) {
        secondSurname = newSurname
        errorMessage = null
    }

    fun onAgeChanged(newAge: String) {
        if (newAge.isEmpty() || newAge.all { it.isDigit() }) {
            age = newAge
            errorMessage = null
        }
    }

    fun onGenderChanged(newGender: Gender) {
        gender = newGender
        errorMessage = null
    }

    fun onBioChanged(newBio: String) {
        bio = newBio
        errorMessage = null
    }

    fun onJobChanged(newJob: String) {
        job = newJob
        errorMessage = null
    }

    fun onHobbyToggled(hobby: Hobby) {
        selectedHobbies = if (selectedHobbies.contains(hobby)) {
            selectedHobbies - hobby
        } else {
            selectedHobbies + hobby
        }
        errorMessage = null
    }

    fun onMainPhotoSelected(uri: String) {
        mainPhotoUri = uri
        errorMessage = null
    }

    fun onGalleryPhotoAdded(uri: String) {
        if (galleryPhotoUris.size < 5) {
            galleryPhotoUris = galleryPhotoUris + uri
            errorMessage = null
        } else {
            errorMessage = "Máximo 5 fotos en la galería"
        }
    }

    fun onGalleryPhotoRemoved(uri: String) {
        galleryPhotoUris = galleryPhotoUris - uri
    }

    fun onSaveProfile(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null

            try {
                Log.d("ProfileEditVM", "=== Iniciando guardado de perfil ===")

                // Validaciones
                val ageInt = age.toIntOrNull()
                if (ageInt == null || ageInt < 18 || ageInt > 100) {
                    errorMessage = "La edad debe estar entre 18 y 100 años"
                    isLoading = false
                    return@launch
                }

                if (name.isBlank() || firstSurname.isBlank()) {
                    errorMessage = "El nombre y primer apellido son obligatorios"
                    isLoading = false
                    return@launch
                }

                if (gender == Gender.UNDEFINED) {
                    errorMessage = "Selecciona tu género"
                    isLoading = false
                    return@launch
                }

                Log.d("ProfileEditVM", "Validaciones básicas OK")
                Log.d("ProfileEditVM", "Usuario autenticado: ${authRepository.currentUserId}")
                Log.d("ProfileEditVM", "Fotos en galería: ${galleryPhotoUris.size}")

                // Subir fotos de galería si hay nuevas
                val uploadedGalleryUrls = if (galleryPhotoUris.isNotEmpty()) {
                    Log.d("ProfileEditVM", "Iniciando subida de ${galleryPhotoUris.size} fotos...")
                    val galleryResult = uploadGalleryImagesUseCase(galleryPhotoUris, context)
                    galleryResult.getOrElse { error ->
                        Log.e("ProfileEditVM", "Error en subida de galería", error)
                        errorMessage = "Error al subir fotos: ${error.message}"
                        isLoading = false
                        return@launch
                    }
                } else {
                    Log.d("ProfileEditVM", "No hay fotos en galería para subir")
                    emptyList()
                }

                Log.d("ProfileEditVM", "Fotos subidas exitosamente: ${uploadedGalleryUrls.size}")

                val profile = UserProfile(
                    name = name,
                    firstSurname = firstSurname,
                    secondSurname = secondSurname,
                    age = ageInt,
                    gender = gender,
                    bio = bio,
                    hobbies = selectedHobbies,
                    mainPhotoUrl = mainPhotoUri ?: "",
                    galleryPhotoUrls = uploadedGalleryUrls,
                    job = job.ifBlank { null },
                    location = null
                )

                Log.d("ProfileEditVM", "Perfil construido, llamando a CompleteProfileUseCase...")

                val result = completeProfileUseCase(
                    profile = profile,
                    localPhotoUri = if (mainPhotoUri?.startsWith("https://") == false) mainPhotoUri else null,
                    context = context
                )

                result.fold(
                    onSuccess = {
                        Log.d("ProfileEditVM", "✅ Perfil guardado exitosamente")
                        successMessage = "Perfil actualizado correctamente"
                        onSuccess()
                    },
                    onFailure = { error ->
                        Log.e("ProfileEditVM", "❌ Error al guardar perfil", error)
                        errorMessage = "Error: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                Log.e("ProfileEditVM", "❌ Excepción inesperada", e)
                errorMessage = "Error inesperado: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}