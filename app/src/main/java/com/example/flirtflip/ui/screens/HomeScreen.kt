package com.example.flirtflip.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.flirtflip.domain.model.Hobby
import com.example.flirtflip.domain.model.User
import com.example.flirtflip.ui.theme.Spacing
import com.example.flirtflip.ui.theme.ThemePreference
import com.example.flirtflip.ui.theme.boostOrange
import com.example.flirtflip.ui.theme.error
import com.example.flirtflip.ui.theme.errorContainer
import com.example.flirtflip.ui.theme.matchColor
import com.example.flirtflip.ui.theme.onBackground
import com.example.flirtflip.ui.theme.onSurfaceVariant
import com.example.flirtflip.ui.theme.primary
import com.example.flirtflip.ui.theme.secondaryContainer
import com.example.flirtflip.ui.theme.softPinkSurface
import com.example.flirtflip.ui.theme.superLikeBlue
import com.example.flirtflip.ui.viewmodel.MatchesViewModel
import com.example.flirtflip.ui.viewmodel.ProfileViewModel
import com.example.flirtflip.ui.viewmodel.SwipeUiState
import com.example.flirtflip.ui.viewmodel.SwipeViewModel
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun HomeScreen(
    swipeViewModel: SwipeViewModel = hiltViewModel(),
    matchesViewModel: MatchesViewModel = hiltViewModel(),
    onNavigateToChatList: () -> Unit = {},
    onNavigateToChatDetail: (String) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToLegal: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    savedStateHandle: SavedStateHandle? = null,
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    onThemeChange: (ThemePreference) -> Unit = {}
) {
    val uiState by swipeViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showPreferences by remember { mutableStateOf(false) }
    val profileViewModel: ProfileViewModel = hiltViewModel()

    // Detectar vuelta desde editar perfil: ir a pestaña perfil y recargar datos
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow("goToProfileTab", false)?.collect { go ->
            if (go) {
                selectedTab = 3
                showPreferences = false
                profileViewModel.loadProfile()
                savedStateHandle.set("goToProfileTab", false)
            }
        }
    }

    Scaffold(
        bottomBar = {
            FlirtFlipBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    if (tab == 2) onNavigateToChatList() else selectedTab = tab
                }
            )
        },
        containerColor = softPinkSurface
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFFFF0F7), Color(0xFFFFE4F0))))
                .padding(paddingValues)
        ) {
            if (showPreferences) {
                PreferencesPanel(
                    themePreference = themePreference,
                    onThemeChange = onThemeChange,
                    onNavigateToLegal = onNavigateToLegal,
                    onNavigateToLogin = onNavigateToLogin,
                    onClose = { showPreferences = false },
                    swipeViewModel = swipeViewModel,
                    profileViewModel = profileViewModel

                )
            } else {
                when (selectedTab) {
                    0 -> ExploreTab(
                        uiState, swipeViewModel, onNavigateToChatDetail,
                        onOpenPreferences = { showPreferences = true }
                    )
                    1 -> MatchesTab(matchesViewModel, onNavigateToChatDetail)
                    3 -> ProfileTab(
                        onNavigateToProfile = onNavigateToProfile,
                        profileViewModel = profileViewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun ExploreTab(
    uiState: SwipeUiState,
    viewModel: SwipeViewModel,
    onNavigateToChatDetail: (String) -> Unit,
    onOpenPreferences: () -> Unit = {}
) {
    val myHobbies by viewModel.myHobbies.collectAsState()
    var showNotifications by remember { mutableStateOf(false) }
    // Simulamos notificaciones basadas en matches recientes
    val hasNewNotifications by remember { mutableStateOf(true) }

    if (showNotifications) {
        NotificationsDialog(onDismiss = { showNotifications = false })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HomeTopBar(
            onOpenPreferences = onOpenPreferences,
            onOpenNotifications = { showNotifications = true },
            hasNewNotifications = hasNewNotifications
        )
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = Spacing.medium),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is SwipeUiState.Loading -> CircularProgressIndicator(color = primary)
                is SwipeUiState.Success -> {
                    if (state.profiles.isNotEmpty()) {
                        val currentUser = state.profiles[state.currentIndex]
                        if (state.currentIndex + 1 < state.profiles.size) {
                            ProfileCard(
                                user = state.profiles[state.currentIndex + 1],
                                modifier = Modifier.fillMaxWidth().height(520.dp).padding(Spacing.medium)
                            )
                        }
                        ProfileCard(user = currentUser, modifier = Modifier.fillMaxWidth().height(520.dp))
                    } else if (!state.showMatchDialog) {
                        NoMoreProfilesCard()
                    }
                    if (state.showMatchDialog && state.matchedUser != null) {
                        MatchDialog(
                            matchedUser = state.matchedUser,
                            myHobbies = myHobbies,
                            onDismiss = { viewModel.dismissMatchDialog() },
                            onSendMessage = {
                                val myId = viewModel.currentUserId
                                val otherId = state.matchedUser.id
                                if (myId != null) {
                                    val chatId = listOfNotNull(myId, otherId).sorted().joinToString("_")
                                    viewModel.dismissMatchDialog()
                                    onNavigateToChatDetail(chatId)
                                }
                            },
                            onNavigateToChatDetail = { chatId, _ ->
                                viewModel.dismissMatchDialog()
                                onNavigateToChatDetail(chatId)
                            },
                            currentUserId = viewModel.currentUserId
                        )
                    }
                }
                is SwipeUiState.Empty -> NoMoreProfilesCard()
                is SwipeUiState.Error -> Text("Error: ${state.message}", color = error)
            }
        }
        if (uiState is SwipeUiState.Success && (uiState as SwipeUiState.Success).profiles.isNotEmpty()) {
            val success = uiState as SwipeUiState.Success
            val current = success.profiles[success.currentIndex]
            SwipeActionButtons(
                onDislike = { viewModel.onSwipe(current, false) },
                onLike = { viewModel.onSwipe(current, true) },
                onSuperLike = { viewModel.onSwipe(current, true) },
                onBoost = { }
            )
        }
        Spacer(modifier = Modifier.height(Spacing.medium))
    }
}

// ─── FIX 1: MatchesTab muestra todos los matches (incluyendo chats activos) ──

@Composable
private fun MatchesTab(
    viewModel: MatchesViewModel,
    onNavigateToChatDetail: (String) -> Unit = {}
) {
    val matchedUsers by viewModel.matchedUsers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.medium)
    ) {
        Text("Matches", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = primary)
        Spacer(modifier = Modifier.height(Spacing.medium))

        if (isLoading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primary)
            }
        } else {
            if (matchedUsers.isEmpty()) {
                Box(
                    Modifier.fillMaxWidth().padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Favorite, null,
                            modifier = Modifier.size(64.dp), tint = primary.copy(0.3f)
                        )
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        Text("Aún no tienes matches", fontWeight = FontWeight.Bold, color = onSurfaceVariant)
                        Text("¡Sigue deslizando!", fontSize = 13.sp, color = onSurfaceVariant)
                    }
                }
            } else {
                Text(
                    "Tus matches (${matchedUsers.size})",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp, color = onBackground
                )
                Spacer(modifier = Modifier.height(Spacing.small))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(((matchedUsers.size / 2 + 1) * 220).dp),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    items(matchedUsers) { user ->
                        MatchUserCard(
                            user = user,
                            onClick = {
                                val chatId = listOf(viewModel.currentUserId, user.id).sorted().joinToString("_")
                                onNavigateToChatDetail(chatId)
                            },
                            onEnviarHola = {
                                viewModel.sendHello(user.id) { chatId ->
                                    onNavigateToChatDetail(chatId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchUserCard(user: User, onClick: () -> Unit, onEnviarHola: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.height(230.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4F0)),
        onClick = onClick
    ) {
        Box(Modifier.fillMaxSize()) {
            // Fondo con Degradado
            Box(
                Modifier.fillMaxSize().background(
                    Brush.linearGradient(listOf(Color(0xFFFFE4F0), Color(0xFFFFD6EC)))
                )
            ) {
                Icon(
                    Icons.Default.Person, null,
                    modifier = Modifier.size(60.dp).align(Alignment.Center),
                    tint = primary.copy(alpha = 0.4f)
                )
            }

            // Imagen de perfil
            if (user.profile.mainPhotoUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.profile.mainPhotoUrl).crossfade(true).build(),
                    contentDescription = user.profile.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // --- AQUÍ ESTÁ EL CAMBIO PRINCIPAL ---
            // Agrupamos el degradado negro, el texto y el botón en una columna inferior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter) // Todo el bloque se alinea abajo
                    .background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.8f)))
                    )
                    .padding(horizontal = 8.dp, vertical = 8.dp) // Controla el padding general
            ) {
                // 1. El Texto del usuario
                Text(
                    text = "${user.profile.name}, ${user.profile.age}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp) // Separación inferior antes del botón
                )

                // 2. El Botón (ahora se posiciona automáticamente debajo)
                Button(
                    onClick = onEnviarHola,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = primary),
                    contentPadding = PaddingValues(vertical = 8.dp) // Un padding interno cómodo
                ) {
                    Icon(
                        Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                    // Volvemos a poner width aquí porque el Icono y el Texto van de lado a lado
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Enviar mensaje", fontSize = 12.sp, color = Color.White)
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .background(primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Favorite, null,
                    modifier = Modifier.size(12.dp),
                    tint = Color.White
                )
            }
        }
    }
}


@Composable
private fun PreferencesPanel(
    themePreference: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit,
    onNavigateToLegal: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    onClose: () -> Unit,
    swipeViewModel: SwipeViewModel,
    profileViewModel: ProfileViewModel
) {
    val savedAgeRange by swipeViewModel.ageRange.collectAsState()
    var ageRange by remember(savedAgeRange) { mutableStateOf(savedAgeRange) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirm = false
                        profileViewModel.signOut { onNavigateToLogin() }
                    }
                ) {
                    Text("Cerrar sesión", color = error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.medium)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, "Cerrar", tint = primary)
            }
            Text("Preferencias", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = primary)
        }

        Spacer(modifier = Modifier.height(Spacing.large))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(Spacing.medium)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, null, tint = primary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(Spacing.small))
                    Text("Rango de edad", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = onBackground)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "${ageRange.start.toInt()} - ${ageRange.endInclusive.toInt()} años",
                        fontSize = 13.sp, color = primary, fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.small))
                RangeSlider(
                    value = ageRange,
                    onValueChange = { ageRange = it },
                    onValueChangeFinished = { swipeViewModel.applyAgeFilter(ageRange) },
                    valueRange = 18f..80f,
                    steps = 61,
                    colors = SliderDefaults.colors(
                        thumbColor = primary,
                        activeTrackColor = primary,
                        inactiveTrackColor = primary.copy(alpha = 0.2f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.medium))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(Spacing.medium)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, null, tint = primary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(Spacing.small))
                    Text("Tema", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = onBackground)
                }
                Spacer(modifier = Modifier.height(Spacing.small))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    listOf(
                        Triple("Claro", Icons.Default.LightMode, ThemePreference.LIGHT),
                        Triple("Oscuro", Icons.Default.DarkMode, ThemePreference.DARK),
                        Triple("Sistema", Icons.Default.SettingsBrightness, ThemePreference.SYSTEM)
                    ).forEach { (label, icon, pref) ->
                        Surface(
                            onClick = { onThemeChange(pref) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = if (themePreference == pref) primary.copy(alpha = 0.12f) else Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (themePreference == pref) 2.dp else 1.dp,
                                color = if (themePreference == pref) primary else onSurfaceVariant.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    icon, label,
                                    tint = if (themePreference == pref) primary else onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    label, fontSize = 12.sp,
                                    fontWeight = if (themePreference == pref) FontWeight.Bold else FontWeight.Normal,
                                    color = if (themePreference == pref) primary else onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.medium))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            onClick = onNavigateToLegal
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(Spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Gavel, null, tint = primary, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    "Información Legal", fontSize = 15.sp, fontWeight = FontWeight.Medium,
                    color = onBackground, modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ChevronRight, null, tint = onSurfaceVariant, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(modifier = Modifier.height(Spacing.medium))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F0)),
            elevation = CardDefaults.cardElevation(2.dp),
            onClick = { showLogoutConfirm = true }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(Spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Logout, null, tint = error, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(Spacing.small))
                Text(
                    "Cerrar sesión", fontSize = 15.sp, fontWeight = FontWeight.Medium,
                    color = error, modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ChevronRight, null, tint = error.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
            }
        }

        Spacer(modifier = Modifier.height(Spacing.extraLarge))
    }
}

// ─── FIX 3: ProfileTab — vista del perfil con bio, intereses y galería ───────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileTab(
    onNavigateToProfile: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val currentUser by profileViewModel.currentUser.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header con foto de perfil
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(listOf(Color(0xFFFF4081), Color(0xFFFFAEC9)))
                ),
            contentAlignment = Alignment.BottomStart
        ) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (currentUser?.profile?.mainPhotoUrl?.isNotBlank() == true) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(currentUser!!.profile.mainPhotoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                            )
                        )
                )
            } else {
                // Placeholder si no hay foto
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person, null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            if (!isLoading) {
                Column(modifier = Modifier.padding(Spacing.large)) {
                    Text(
                        text = if (currentUser != null)
                            "${currentUser!!.profile.name}, ${currentUser!!.profile.age}"
                        else "Tu perfil",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    if (currentUser?.profile?.job?.isNotBlank() == true) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Work, null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentUser!!.profile.job ?: "",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.medium))

        Column(modifier = Modifier.padding(horizontal = Spacing.medium)) {

            // Botón editar perfil
            Button(
                onClick = onNavigateToProfile,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = primary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar perfil", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            if (!isLoading && currentUser != null) {

                // Bio
                if (currentUser!!.profile.bio.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(Spacing.medium)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FormatQuote, null, tint = primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sobre mí", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentUser!!.profile.bio,
                                fontSize = 14.sp,
                                color = onBackground,
                                lineHeight = 21.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.medium))
                }

                // Intereses
                if (currentUser!!.profile.hobbies.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(Spacing.medium)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Tag, null, tint = primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Intereses", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primary)
                            }
                            Spacer(modifier = Modifier.height(Spacing.small))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                currentUser!!.profile.hobbies.forEach { hobby ->
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = primary.copy(alpha = 0.12f),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp, primary.copy(alpha = 0.3f)
                                        )
                                    ) {
                                        Text(
                                            text = hobbyDisplayName(hobby),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            fontSize = 13.sp,
                                            color = primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.medium))
                }

                // Galería de fotos
                if (currentUser!!.profile.galleryPhotoUrls.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(Spacing.medium)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PhotoLibrary, null, tint = primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Galería", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primary)
                            }
                            Spacer(modifier = Modifier.height(Spacing.small))
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                modifier = Modifier.height(
                                    ((currentUser!!.profile.galleryPhotoUrls.size / 3 + 1) * 100).dp
                                ),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(currentUser!!.profile.galleryPhotoUrls) { url ->
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(url).crossfade(true).build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .height(96.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                }
                            }
                        }
                    }
                }

            } else if (!isLoading && currentUser == null) {
                // Sin perfil todavía
                Box(
                    Modifier.fillMaxWidth().padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Completa tu perfil", fontWeight = FontWeight.Bold, color = onSurfaceVariant)
                        Text("Añade tu info para que otros te conozcan", fontSize = 13.sp, color = onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.extraLarge))
        }
    }
}

private fun hobbyDisplayName(hobby: Hobby): String = when (hobby) {
    Hobby.SPORTS -> "⚽ Deportes"
    Hobby.MUSIC -> "🎵 Música"
    Hobby.TRAVEL -> "✈️ Viajes"
    Hobby.COOKING -> "🍳 Cocina"
    Hobby.GAMING -> "🎮 Videojuegos"
    Hobby.ART -> "🎨 Arte"
    Hobby.MOVIES -> "🎬 Películas"
    Hobby.READING -> "📚 Lectura"
    Hobby.GYM -> "💪 Gimnasio"
}

// ─── Componentes compartidos ─────────────────────────────────────────────────

@Composable
fun MatchDialog(
    matchedUser: User,
    myHobbies: List<Hobby> = emptyList(),
    currentUserId: String? = null,
    onDismiss: () -> Unit,
    onSendMessage: () -> Unit = {},
    onNavigateToChatDetail: (String, String) -> Unit = { _, _ -> }
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onSendMessage,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("💬 Enviar mensaje")
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seguir deslizando", color = onSurfaceVariant)
                }
            }
        },
        dismissButton = {},
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔥", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("¡Es un Match!", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = primary)
            }
        },
        text = {
            Text(
                "A ${matchedUser.profile.name} también le gustas. ¡Empieza la conversación!",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = onSurfaceVariant
            )
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@Composable
fun ProfileCard(user: User, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.shadow(16.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.linearGradient(listOf(Color(0xFFFFE4F0), Color(0xFFFFD6EC)))
                )
            ) {
                Icon(
                    Icons.Default.Person, null,
                    modifier = Modifier.size(100.dp).align(Alignment.Center),
                    tint = primary.copy(0.4f)
                )
            }
            if (user.profile.mainPhotoUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.profile.mainPhotoUrl).crossfade(true).build(),
                    contentDescription = user.profile.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth().fillMaxHeight(0.6f).align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f))))
            )
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(Spacing.large)) {
                Text(
                    "${user.profile.name}, ${user.profile.age}",
                    color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold
                )
                Text(user.profile.bio, color = Color.White.copy(0.8f), fontSize = 14.sp, maxLines = 2)
            }
        }
    }
}

@Composable
fun SwipeActionButtons(onDislike: () -> Unit, onSuperLike: () -> Unit, onLike: () -> Unit, onBoost: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.extraLarge),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton(Icons.Default.Close, error, errorContainer, 60.dp, onDislike)
        ActionButton(Icons.Default.StarRate, superLikeBlue, Color(0xFFE3F2FD), 52.dp, onSuperLike)
        ActionButton(Icons.Default.Favorite, matchColor, secondaryContainer, 60.dp, onLike)
        ActionButton(Icons.Default.Bolt, boostOrange, Color(0xFFFBE9E7), 52.dp, onBoost)
    }
}

@Composable
fun ActionButton(icon: ImageVector, tint: Color, background: Color, size: Dp, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(size)) {
        Box(
            modifier = Modifier.fillMaxSize().shadow(6.dp, CircleShape).background(background, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(size * 0.45f))
        }
    }
}

@Composable
fun NoMoreProfilesCard() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(80.dp), tint = primary)
        Text("¡No hay más perfiles por ahora!", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun HomeTopBar(
    onOpenPreferences: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    hasNewNotifications: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.medium, vertical = Spacing.small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onOpenPreferences) {
            Icon(Icons.Default.Tune, "Preferencias", tint = primary)
        }
        Text("FlirtFlip", color = primary, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        Box {
            IconButton(onClick = onOpenNotifications) {
                Icon(
                    imageVector = if (hasNewNotifications) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                    contentDescription = "Notificaciones",
                    tint = primary
                )
            }
            if (hasNewNotifications) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(androidx.compose.ui.graphics.Color(0xFFFF4081), CircleShape)
                        .align(Alignment.TopEnd)
                        .padding(1.dp)
                )
            }
        }
    }
}

// ─── Panel de Notificaciones ─────────────────────────────────────────────────

private data class AppNotification(
    val icon: String,
    val title: String,
    val body: String,
    val time: String
)

private val sampleNotifications = listOf(
    AppNotification("❤️", "¡Nuevo Match!", "Has hecho match con alguien. ¡Envíale un mensaje!", "Hace 2 min"),
    AppNotification("💬", "Mensaje nuevo", "Tienes un mensaje sin leer en tus chats.", "Hace 15 min"),
    AppNotification("⭐", "Super Like recibido", "Alguien te ha dado un Super Like. ¡Revísalo!", "Hace 1 h"),
    AppNotification("🔥", "Completa tu perfil", "Los perfiles completos reciben 3x más matches.", "Hace 3 h"),
)

@Composable
private fun NotificationsDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = androidx.compose.ui.graphics.Color.White,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Notificaciones",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = primary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, null, tint = onSurfaceVariant)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                sampleNotifications.forEach { notif ->
                    NotificationItem(notif)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Las notificaciones en tiempo real se activan automáticamente al recibir matches o mensajes.",
                    fontSize = 11.sp,
                    color = onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun NotificationItem(notif: AppNotification) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFFFF0F7)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(notif.icon, fontSize = 26.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = onBackground)
                Text(notif.body, fontSize = 12.sp, color = onSurfaceVariant, lineHeight = 17.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(notif.time, fontSize = 10.sp, color = onSurfaceVariant)
        }
    }
}

@Composable
private fun FlirtFlipBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        val items = listOf(
            "Explorar" to Icons.Default.Explore,
            "Matches" to Icons.Default.Favorite,
            "Chats" to Icons.Default.Chat,
            "Perfil" to Icons.Default.Person
        )
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = { Icon(item.second, null) },
                label = { Text(item.first) }
            )
        }
    }
}