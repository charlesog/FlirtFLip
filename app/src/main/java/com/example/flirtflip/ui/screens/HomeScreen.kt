package com.example.flirtflip.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flirtflip.domain.model.User
import com.example.flirtflip.ui.theme.*
import com.example.flirtflip.ui.viewmodel.MatchesViewModel
import com.example.flirtflip.ui.viewmodel.SwipeUiState
import com.example.flirtflip.ui.viewmodel.SwipeViewModel

@Composable
fun HomeScreen(
    viewModel: SwipeViewModel = hiltViewModel(),
    matchesViewModel: MatchesViewModel = hiltViewModel(),
    themePreference: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit,
    onNavigateToMatches: () -> Unit = {},
    onNavigateToChatList: () -> Unit = {},
    onNavigateToChatDetail: (String) -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val matchedUsers by matchesViewModel.matchedUsers.collectAsState()
    val isLoadingMatches by matchesViewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showPreferences by remember { mutableStateOf(false) }

    // Estado de preferencias
    var distanceRange by remember { mutableFloatStateOf(50f) }
    var ageMin by remember { mutableFloatStateOf(18f) }
    var ageMax by remember { mutableFloatStateOf(40f) }

    Scaffold(
        bottomBar = {
            FlirtFlipBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onChatClick = onNavigateToChatList,
                onProfileClick = onNavigateToProfile
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
            Column(modifier = Modifier.fillMaxSize()) {

                HomeTopBar(
                    onFiltersClick = { showPreferences = true }
                )

                when (selectedTab) {
                    0 -> SwipeTab(
                        modifier = Modifier.weight(1f),
                        uiState = uiState,
                        onSwipe = { user, liked -> viewModel.onSwipe(user, liked) },
                        onDismissMatch = { viewModel.dismissMatchDialog() },
                        onNavigateToChatDetail = onNavigateToChatDetail,
                        currentUserId = viewModel.currentUserId
                    )
                    1 -> MatchesTab(
                        modifier = Modifier.weight(1f),
                        matchedUsers = matchedUsers,
                        isLoading = isLoadingMatches,
                        onChatClick = onNavigateToChatDetail
                    )
                }
            }
        }
    }

    // Diálogo de preferencias
    if (showPreferences) {
        PreferencesDialog(
            themePreference = themePreference,
            onThemeChange = onThemeChange,
            distanceRange = distanceRange,
            onDistanceChange = { distanceRange = it },
            ageMin = ageMin,
            ageMax = ageMax,
            onAgeMinChange = { ageMin = it },
            onAgeMaxChange = { ageMax = it },
            onDismiss = { showPreferences = false }
        )
    }
}

// ── Diálogo de Preferencias ───────────────────────────────────────────────────

@Composable
private fun PreferencesDialog(
    themePreference: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit,
    distanceRange: Float,
    onDistanceChange: (Float) -> Unit,
    ageMin: Float,
    ageMax: Float,
    onAgeMinChange: (Float) -> Unit,
    onAgeMaxChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(Spacing.large)) {

                // Cabecera
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Preferencias",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = onSurfaceVariant)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.small), color = surfaceVariant)

                // ── DISTANCIA ───────────────────────────────────────────
                PrefSectionTitle("Distancia máxima")
                Text(
                    text = "${distanceRange.toInt()} km",
                    fontSize = 14.sp,
                    color = primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End)
                )
                Slider(
                    value = distanceRange,
                    onValueChange = onDistanceChange,
                    valueRange = 1f..200f,
                    colors = SliderDefaults.colors(
                        thumbColor = primary,
                        activeTrackColor = primary,
                        inactiveTrackColor = surfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.small))

                // ── EDAD ────────────────────────────────────────────────
                PrefSectionTitle("Rango de edad")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${ageMin.toInt()} años", fontSize = 13.sp, color = primary, fontWeight = FontWeight.Bold)
                    Text("${ageMax.toInt()} años", fontSize = 13.sp, color = primary, fontWeight = FontWeight.Bold)
                }
                // Slider mínimo
                Slider(
                    value = ageMin,
                    onValueChange = { if (it < ageMax - 1) onAgeMinChange(it) },
                    valueRange = 18f..80f,
                    colors = SliderDefaults.colors(
                        thumbColor = primary,
                        activeTrackColor = primary,
                        inactiveTrackColor = surfaceVariant
                    )
                )
                // Slider máximo
                Slider(
                    value = ageMax,
                    onValueChange = { if (it > ageMin + 1) onAgeMaxChange(it) },
                    valueRange = 18f..80f,
                    colors = SliderDefaults.colors(
                        thumbColor = secondary,
                        activeTrackColor = secondary,
                        inactiveTrackColor = surfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.small))

                // ── TEMA ────────────────────────────────────────────────
                PrefSectionTitle("Tema")
                Spacer(modifier = Modifier.height(Spacing.small))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    ThemeChip(
                        label = "Claro",
                        icon = Icons.Default.LightMode,
                        selected = themePreference == ThemePreference.LIGHT,
                        onClick = { onThemeChange(ThemePreference.LIGHT) },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeChip(
                        label = "Oscuro",
                        icon = Icons.Default.DarkMode,
                        selected = themePreference == ThemePreference.DARK,
                        onClick = { onThemeChange(ThemePreference.DARK) },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeChip(
                        label = "Sistema",
                        icon = Icons.Default.SettingsBrightness,
                        selected = themePreference == ThemePreference.SYSTEM,
                        onClick = { onThemeChange(ThemePreference.SYSTEM) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.medium))

                // Botón aplicar
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primary)
                ) {
                    Text("Aplicar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun PrefSectionTitle(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 2.dp)
    )
}

@Composable
private fun ThemeChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) primary.copy(alpha = 0.12f) else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) primary else onSurfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) primary else onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) primary else onSurfaceVariant
            )
        }
    }
}

// ── Tab Swipe ─────────────────────────────────────────────────────────────────

@Composable
private fun SwipeTab(
    modifier: Modifier = Modifier,
    uiState: SwipeUiState,
    onSwipe: (User, Boolean) -> Unit,
    onDismissMatch: () -> Unit,
    onNavigateToChatDetail: (String) -> Unit,
    currentUserId: String?
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium),
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

                    Column(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = Spacing.medium)
                    ) {
                        SwipeActionButtons(
                            onDislike = { onSwipe(currentUser, false) },
                            onLike = { onSwipe(currentUser, true) },
                            onSuperLike = { onSwipe(currentUser, true) },
                            onBoost = {}
                        )
                    }
                } else if (!state.showMatchDialog) {
                    NoMoreProfilesCard()
                }

                if (state.showMatchDialog && state.matchedUser != null) {
                    MatchDialog(
                        matchedUser = state.matchedUser,
                        onDismiss = onDismissMatch,
                        onSendMessage = {
                            val otherId = state.matchedUser.id
                            if (currentUserId != null) {
                                val chatId = listOf(currentUserId, otherId).sorted().joinToString("_")
                                onDismissMatch()
                                onNavigateToChatDetail(chatId)
                            }
                        }
                    )
                }
            }

            is SwipeUiState.Empty -> NoMoreProfilesCard()
            is SwipeUiState.Error -> Text("Error: ${state.message}", color = error)
        }
    }
}

// ── Tab Matches ───────────────────────────────────────────────────────────────

@Composable
private fun MatchesTab(
    modifier: Modifier = Modifier,
    matchedUsers: List<User>,
    isLoading: Boolean,
    onChatClick: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.medium)
    ) {
        Text(
            text = "Tus Matches",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            color = primary,
            modifier = Modifier.padding(vertical = Spacing.medium)
        )

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primary)
                }
            }
            matchedUsers.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = primary.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        Text("Aún no tienes matches", fontWeight = FontWeight.Bold, color = onBackground)
                        Text("¡Sigue deslizando!", color = onSurfaceVariant, fontSize = 14.sp)
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    items(matchedUsers) { user ->
                        MatchCard(user = user, onClick = {
                            // chatId consistente con el resto de la app
                            onChatClick(user.id)
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchCard(user: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Fondo placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(listOf(Color(0xFFFFE4F0), Color(0xFFFFD6EC)))
                    )
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp).align(Alignment.Center),
                    tint = primary.copy(alpha = 0.4f)
                )
            }
            if (user.profile.mainPhotoUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.profile.mainPhotoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = user.profile.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            // Gradiente inferior con nombre
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.65f)))
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(Spacing.small)
            ) {
                Text(
                    text = user.profile.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "${user.profile.age} años",
                    color = Color.White.copy(0.85f),
                    fontSize = 12.sp
                )
            }
            // Badge "Nuevo match"
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp),
                shape = RoundedCornerShape(8.dp),
                color = primary
            ) {
                Text(
                    text = "Match",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}

// ── Componentes existentes ────────────────────────────────────────────────────

@Composable
private fun MatchDialog(matchedUser: User, onDismiss: () -> Unit, onSendMessage: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onSendMessage, colors = ButtonDefaults.buttonColors(containerColor = primary)) {
                Text("Enviar mensaje")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Seguir deslizando", color = primary) }
        },
        title = { Text("¡Es un Match! 🔥", fontWeight = FontWeight.Bold) },
        text = { Text("A ${matchedUser.profile.name} también le gustas.") },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@Composable
private fun ProfileCard(user: User, modifier: Modifier = Modifier) {
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
                        .data(user.profile.mainPhotoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = user.profile.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f))))
            )
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(Spacing.large)) {
                Text("${user.profile.name}, ${user.profile.age}", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                Text(user.profile.bio, color = Color.White.copy(0.8f), fontSize = 14.sp, maxLines = 2)
            }
        }
    }
}

@Composable
private fun SwipeActionButtons(onDislike: () -> Unit, onSuperLike: () -> Unit, onLike: () -> Unit, onBoost: () -> Unit) {
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
private fun ActionButton(icon: ImageVector, tint: Color, background: Color, size: Dp, onClick: () -> Unit) {
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
private fun NoMoreProfilesCard() {
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
private fun HomeTopBar(onFiltersClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onFiltersClick) {
            Icon(Icons.Default.Tune, "Preferencias", tint = primary)
        }
        Text("FlirtFlip", color = primary, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        IconButton(onClick = { }) {
            Icon(Icons.Default.Notifications, "Notificaciones", tint = primary)
        }
    }
}

@Composable
private fun FlirtFlipBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onChatClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        val items = listOf(
            "Explorar" to Icons.Default.Explore,
            "Matches" to Icons.Default.Favorite,
            "Chats" to Icons.Default.Chat,
            "Perfil" to Icons.Default.Person
        )
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = {
                    onTabSelected(index)
                    when (index) {
                        2 -> onChatClick()
                        3 -> onProfileClick()
                    }
                },
                icon = { Icon(item.second, null) },
                label = { Text(item.first) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primary,
                    selectedTextColor = primary,
                    indicatorColor = primaryContainer
                )
            )
        }
    }
}