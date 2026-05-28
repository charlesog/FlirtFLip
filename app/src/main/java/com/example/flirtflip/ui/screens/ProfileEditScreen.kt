package com.example.flirtflip.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.flirtflip.domain.model.Gender
import com.example.flirtflip.domain.model.Hobby
import com.example.flirtflip.ui.theme.Spacing
import com.example.flirtflip.ui.theme.error
import com.example.flirtflip.ui.theme.errorContainer
import com.example.flirtflip.ui.theme.onBackground
import com.example.flirtflip.ui.theme.onErrorContainer
import com.example.flirtflip.ui.theme.onSurface
import com.example.flirtflip.ui.theme.onSurfaceVariant
import com.example.flirtflip.ui.theme.onlineGreen
import com.example.flirtflip.ui.theme.primary
import com.example.flirtflip.ui.theme.primaryContainer
import com.example.flirtflip.ui.theme.secondaryContainer
import com.example.flirtflip.ui.theme.softPinkSurface
import com.example.flirtflip.ui.viewmodel.ProfileEditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    viewModel: ProfileEditViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.onMainPhotoSelected(it.toString())
        }
    }

    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.onGalleryPhotoAdded(it.toString())
        }
    }

    Scaffold(
        topBar = {
            ProfileEditTopBar(
                title = if (viewModel.isCreatingProfile) "Crear perfil" else "Editar perfil",
                onNavigateBack = onNavigateBack,
                onSave = { viewModel.onSaveProfile(context, onNavigateBack) },
                isLoading = viewModel.isLoading
            )
        },
        containerColor = softPinkSurface
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF0F7),
                            Color(0xFFFFE4F0)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(Spacing.medium)
            ) {
                // Mensajes de error/éxito
                viewModel.errorMessage?.let { error ->
                    ErrorMessage(error) { viewModel.errorMessage = null }
                }

                viewModel.successMessage?.let { success ->
                    SuccessMessage(success) { viewModel.successMessage = null }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Foto de perfil principal
                MainPhotoSection(
                    photoUri = viewModel.mainPhotoUri,
                    onPhotoClick = { photoPickerLauncher.launch("image/*") }
                )

                Spacer(modifier = Modifier.height(Spacing.large))

                // Información básica
                SectionTitle("Información Básica")
                Spacer(modifier = Modifier.height(Spacing.small))

                FlirtFlipTextField(
                    value = viewModel.name,
                    onValueChange = { viewModel.onNameChanged(it) },
                    label = "Nombre",
                    placeholder = "Tu nombre"
                )

                Spacer(modifier = Modifier.height(Spacing.small))

                FlirtFlipTextField(
                    value = viewModel.firstSurname,
                    onValueChange = { viewModel.onFirstSurnameChanged(it) },
                    label = "Primer Apellido",
                    placeholder = "Tu primer apellido"
                )

                Spacer(modifier = Modifier.height(Spacing.small))

                FlirtFlipTextField(
                    value = viewModel.secondSurname,
                    onValueChange = { viewModel.onSecondSurnameChanged(it) },
                    label = "Segundo Apellido",
                    placeholder = "Tu segundo apellido (opcional)"
                )

                Spacer(modifier = Modifier.height(Spacing.small))

                FlirtFlipTextField(
                    value = viewModel.age,
                    onValueChange = { viewModel.onAgeChanged(it) },
                    label = "Edad",
                    placeholder = "Tu edad"
                )

                Spacer(modifier = Modifier.height(Spacing.small))

                FlirtFlipTextField(
                    value = viewModel.job,
                    onValueChange = { viewModel.onJobChanged(it) },
                    label = "Profesión",
                    placeholder = "¿A qué te dedicas?"
                )

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Género
                SectionTitle("Género")
                Spacer(modifier = Modifier.height(Spacing.small))
                GenderSelector(
                    selectedGender = viewModel.gender,
                    onGenderSelected = { viewModel.onGenderChanged(it) }
                )

                Spacer(modifier = Modifier.height(Spacing.large))

                // Biografía
                SectionTitle("Sobre Ti")
                Spacer(modifier = Modifier.height(Spacing.small))
                FlirtFlipTextField(
                    value = viewModel.bio,
                    onValueChange = { if (it.length <= 500) viewModel.onBioChanged(it) },
                    label = "Biografía",
                    placeholder = "Cuéntanos algo sobre ti...",
                    minLines = 4,
                    maxLines = 6
                )
                Text(
                    text = "${viewModel.bio.length}/500",
                    fontSize = 12.sp,
                    color = onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(Spacing.large))

                // Hobbies
                SectionTitle("Intereses")
                Spacer(modifier = Modifier.height(Spacing.small))
                HobbiesSelector(
                    selectedHobbies = viewModel.selectedHobbies,
                    onHobbyToggled = { viewModel.onHobbyToggled(it) }
                )

                Spacer(modifier = Modifier.height(Spacing.large))

                // Galería de fotos
                SectionTitle("Galería de Fotos")
                Text(
                    text = "Añade hasta 5 fotos para que otros puedan conocerte mejor",
                    fontSize = 12.sp,
                    color = onSurfaceVariant,
                    modifier = Modifier.padding(vertical = Spacing.small)
                )
                GallerySection(
                    galleryUris = viewModel.galleryPhotoUris,
                    onAddPhoto = { galleryPickerLauncher.launch("image/*") },
                    onRemovePhoto = { viewModel.onGalleryPhotoRemoved(it) }
                )

                Spacer(modifier = Modifier.height(Spacing.extraLarge))

                // ── AJUSTES ──────────────────────────────────────────────
                SectionTitle("Ajustes")
                Spacer(modifier = Modifier.height(Spacing.small))

                Spacer(modifier = Modifier.height(Spacing.extraLarge))
            }

            // Loading overlay
            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primary)
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = Spacing.medium, vertical = Spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(Spacing.small))
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = onBackground,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ── Componentes originales ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileEditTopBar(
    title: String,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    isLoading: Boolean
) {
    TopAppBar(
        title = {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = primary
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = primary
                )
            }
        },
        actions = {
            TextButton(
                onClick = onSave,
                enabled = !isLoading
            ) {
                Text(
                    "Guardar",
                    color = if (!isLoading) primary else onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
private fun MainPhotoSection(
    photoUri: String?,
    onPhotoClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(primaryContainer, secondaryContainer)
                    )
                )
                .clickable { onPhotoClick() },
            contentAlignment = Alignment.Center
        ) {
            if (photoUri == null) {
                Icon(
                    Icons.Default.AddAPhoto,
                    contentDescription = "Añadir foto",
                    modifier = Modifier.size(48.dp),
                    tint = primary
                )
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(Spacing.small))
        Text(
            text = if (photoUri == null) "Añadir Foto de Perfil" else "Cambiar Foto",
            color = primary,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlirtFlipTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    minLines: Int = 1,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        maxLines = maxLines,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = primary,
            unfocusedIndicatorColor = onSurfaceVariant.copy(alpha = 0.3f),
            focusedLabelColor = primary,
            cursorColor = primary
        )
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = primary
    )
}

@Composable
private fun GenderSelector(
    selectedGender: Gender,
    onGenderSelected: (Gender) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        GenderChip(
            text = "Hombre",
            selected = selectedGender == Gender.MALE,
            onClick = { onGenderSelected(Gender.MALE) },
            modifier = Modifier.weight(1f)
        )
        GenderChip(
            text = "Mujer",
            selected = selectedGender == Gender.FEMALE,
            onClick = { onGenderSelected(Gender.FEMALE) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun GenderChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(24.dp),
        color = if (selected) primary else Color.White,
        border = if (!selected) BorderStroke(1.dp, onSurfaceVariant.copy(alpha = 0.3f)) else null
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                color = if (selected) Color.White else onSurface,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun HobbiesSelector(
    selectedHobbies: List<Hobby>,
    onHobbyToggled: (Hobby) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.height(280.dp),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        verticalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        items(Hobby.entries) { hobby ->
            HobbyChip(
                hobby = hobby,
                selected = selectedHobbies.contains(hobby),
                onClick = { onHobbyToggled(hobby) }
            )
        }
    }
}

@Composable
private fun HobbyChip(
    hobby: Hobby,
    selected: Boolean,
    onClick: () -> Unit
) {
    val hobbyName = when (hobby) {
        Hobby.SPORTS -> "Deportes"
        Hobby.MUSIC -> "Música"
        Hobby.TRAVEL -> "Viajes"
        Hobby.COOKING -> "Cocina"
        Hobby.GAMING -> "Videojuegos"
        Hobby.ART -> "Arte"
        Hobby.MOVIES -> "Películas"
        Hobby.READING -> "Lectura"
        Hobby.GYM -> "Gimnasio"
    }

    val hobbyIcon = when (hobby) {
        Hobby.SPORTS -> Icons.Default.SportsSoccer
        Hobby.MUSIC -> Icons.Default.MusicNote
        Hobby.TRAVEL -> Icons.Default.Flight
        Hobby.COOKING -> Icons.Default.Restaurant
        Hobby.GAMING -> Icons.Default.SportsEsports
        Hobby.ART -> Icons.Default.Palette
        Hobby.MOVIES -> Icons.Default.Movie
        Hobby.READING -> Icons.Default.Book
        Hobby.GYM -> Icons.Default.FitnessCenter
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) primary.copy(alpha = 0.15f) else Color.White,
        border = BorderStroke(
            width = 2.dp,
            color = if (selected) primary else onSurfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier.height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            Icon(
                imageVector = hobbyIcon,
                contentDescription = hobbyName,
                tint = if (selected) primary else onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = hobbyName,
                color = if (selected) primary else onSurface,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun GallerySection(
    galleryUris: List<String>,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(primaryContainer)
                    .border(
                        width = 2.dp,
                        color = primary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { if (galleryUris.size < 5) onAddPhoto() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Añadir foto",
                        tint = primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "${galleryUris.size}/5",
                        color = primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        items(galleryUris) { uri ->
            Box(modifier = Modifier.size(120.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto de galería",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFFE4F0), Color(0xFFFFF0F7))
                            )
                        ),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { onRemovePhoto(uri) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Eliminar",
                        tint = error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorMessage(message: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = errorContainer),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Error, contentDescription = null, tint = error)
                Text(text = message, color = onErrorContainer, fontSize = 14.sp)
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = error)
            }
        }
    }
}

@Composable
private fun SuccessMessage(message: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = onlineGreen)
                Text(text = message, color = Color(0xFF1B5E20), fontSize = 14.sp)
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = onlineGreen)
            }
        }
    }
}