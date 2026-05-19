package com.example.flirtflip.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flirtflip.domain.model.User
import com.example.flirtflip.ui.viewmodel.SwipeViewModel
import com.example.flirtflip.ui.theme.*
import com.example.flirtflip.ui.viewmodel.SwipeUiState

@Composable
fun HomeScreen(
    viewModel: SwipeViewModel = hiltViewModel(),
    onNavigateToMatches: () -> Unit = {},
    onNavigateToChatList: () -> Unit = {},
    onNavigateToChatDetail: (String) -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            FlirtFlipBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onMatchesClick = onNavigateToMatches,
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
                HomeTopBar()

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
                                    onDismiss = { viewModel.dismissMatchDialog() },
                                    onSendMessage = {
                                        val myId = viewModel.currentUserId
                                        val otherId = state.matchedUser.id

                                        if (myId != null) {
                                            // Generar chatId consistente (puedes usar esta lógica o dejar que el Repo lo gestione)
                                            val chatId = listOfNotNull(myId, otherId).sorted().joinToString("_")

                                            viewModel.dismissMatchDialog()
                                            // CORRECCIÓN: El diálogo de Match va directo al DETALLE
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
    }
}


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
            Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFFFFE4F0), Color(0xFFFFD6EC))))) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(100.dp).align(Alignment.Center), tint = primary.copy(0.4f))
            }
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.6f).align(Alignment.BottomCenter).background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f)))))
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(Spacing.large)) {
                Text("${user.profile.name}, ${user.profile.age}", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                Text(user.profile.bio, color = Color.White.copy(0.8f), fontSize = 14.sp, maxLines = 2)
            }
        }
    }
}

@Composable
private fun SwipeActionButtons(onDislike: () -> Unit, onSuperLike: () -> Unit, onLike: () -> Unit, onBoost: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.extraLarge), horizontalArrangement = Arrangement.SpaceEvenly) {
        ActionButton(Icons.Default.Close, error, errorContainer, 60.dp, onDislike)
        ActionButton(Icons.Default.StarRate, superLikeBlue, Color(0xFFE3F2FD), 52.dp, onSuperLike)
        ActionButton(Icons.Default.Favorite, matchColor, secondaryContainer, 60.dp, onLike)
        ActionButton(Icons.Default.Bolt, boostOrange, Color(0xFFFBE9E7), 52.dp, onBoost)
    }
}

@Composable
private fun ActionButton(icon: ImageVector, tint: Color, background: Color, size: Dp, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(size)) {
        Box(modifier = Modifier.fillMaxSize().shadow(6.dp, CircleShape).background(background, CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(size * 0.45f))
        }
    }
}

@Composable
private fun NoMoreProfilesCard() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(80.dp), tint = primary)
        Text("¡No hay más perfiles por ahora!", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun HomeTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.medium, vertical = Spacing.small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) { Icon(Icons.Default.Tune, "Filtros", tint = primary) }
        Text("FlirtFlip", color = primary, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        IconButton(onClick = { }) { Icon(Icons.Default.Notifications, "Notis", tint = primary) }
    }
}

@Composable
private fun FlirtFlipBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onMatchesClick: () -> Unit,
    onChatClick: () -> Unit,
    onProfileClick: () -> Unit
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
                onClick = {
                    onTabSelected(index)
                    when(index) {
                        1 -> onMatchesClick()
                        2 -> onChatClick()
                        3 -> onProfileClick()
                    }
                },
                icon = { Icon(item.second, null) },
                label = { Text(item.first) }
            )
        }
    }
}