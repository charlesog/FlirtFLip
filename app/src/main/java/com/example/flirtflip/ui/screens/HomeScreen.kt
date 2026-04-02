package com.example.flirtflip.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flirtflip.ui.theme.*

private data class PreviewProfile(
    val name: String,
    val age: Int,
    val distance: String,
    val bio: String,
    val tags: List<String>,
    val isVerified: Boolean = false,
    val isPremium: Boolean = false
)

private val sampleProfiles = listOf(
    PreviewProfile(
        name = "Sofia",
        age = 24,
        distance = "2 km",
        bio = "Amante de los viajes y el cafe. Buscando alguien con quien explorar el mundo.",
        tags = listOf("Viajes", "Fotografia", "Yoga"),
        isVerified = true
    ),
    PreviewProfile(
        name = "Carlos",
        age = 27,
        distance = "5 km",
        bio = "Chef de profesion, aventurero de corazon. Me encanta la musica en directo.",
        tags = listOf("Cocina", "Musica", "Fitness"),
        isPremium = true
    )
)

@Composable
fun HomeScreen(
    onNavigateToMatches: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    var currentProfileIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            FlirtFlipBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onMatchesClick = onNavigateToMatches,
                onChatClick = onNavigateToChat,
                onProfileClick = onNavigateToProfile
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
            Column(modifier = Modifier.fillMaxSize()) {
                HomeTopBar()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = Spacing.medium),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentProfileIndex < sampleProfiles.size) {
                        if (currentProfileIndex + 1 < sampleProfiles.size) {
                            ProfileCard(
                                profile = sampleProfiles[currentProfileIndex + 1],
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(520.dp)
                                    .padding(Spacing.medium)
                            )
                        }
                        ProfileCard(
                            profile = sampleProfiles[currentProfileIndex],
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(520.dp)
                        )
                    } else {
                        NoMoreProfilesCard()
                    }
                }

                SwipeActionButtons(
                    onDislike = {
                        if (currentProfileIndex < sampleProfiles.size) currentProfileIndex++
                    },
                    onSuperLike = {
                        if (currentProfileIndex < sampleProfiles.size) currentProfileIndex++
                    },
                    onLike = {
                        if (currentProfileIndex < sampleProfiles.size) currentProfileIndex++
                    },
                    onBoost = { }
                )

                Spacer(modifier = Modifier.height(Spacing.medium))
            }
        }
    }
}

@Composable
private fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) {
            Icon(
                Icons.Default.Tune,
                contentDescription = "Filtros",
                tint = primary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(colors = listOf(pinkGradientStart, pinkGradientEnd))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("FF", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.width(Spacing.small))
            Text(
                text = "FlirtFlip",
                color = primary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )
        }

        Box {
            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    tint = primary
                )
            }
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(secondary, CircleShape)
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 4.dp)
            )
        }
    }
}

@Composable
private fun ProfileCard(
    profile: PreviewProfile,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(
            elevation = 16.dp,
            shape = RoundedCornerShape(24.dp),
            ambientColor = primary.copy(alpha = 0.2f),
            spotColor = secondary.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Fondo con gradiente rosa suave
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFE4F0),
                                Color(0xFFFFF0F7),
                                Color(0xFFFFD6EC)
                            )
                        )
                    )
            ) {
                // Avatar placeholder
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center)
                        .offset(y = (-40).dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(primary.copy(alpha = 0.3f), secondary.copy(alpha = 0.3f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = primary.copy(alpha = 0.8f)
                    )
                }
            }

            // Gradiente inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.55f)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF2D1B22).copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Informacion del perfil
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(Spacing.large)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    Text(
                        text = "${profile.name}, ${profile.age}",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (profile.isVerified) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Verificado",
                            tint = verifiedBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    if (profile.isPremium) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Premium",
                            tint = premiumGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = tertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = profile.distance,
                        color = tertiary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.small))

                Text(
                    text = profile.bio,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(Spacing.small))

                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    profile.tags.take(3).forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = primary.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = tag,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Boton info
            IconButton(
                onClick = { },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Spacing.medium)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Ver perfil",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SwipeActionButtons(
    onDislike: () -> Unit,
    onSuperLike: () -> Unit,
    onLike: () -> Unit,
    onBoost: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.extraLarge),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(
            icon = Icons.Default.Close,
            tint = error,
            background = errorContainer,
            size = 60.dp,
            onClick = onDislike
        )
        ActionButton(
            icon = Icons.Default.StarRate,
            tint = superLikeBlue,
            background = Color(0xFFE3F2FD),
            size = 52.dp,
            onClick = onSuperLike
        )
        ActionButton(
            icon = Icons.Default.Favorite,
            tint = matchColor,
            background = secondaryContainer,
            size = 60.dp,
            onClick = onLike
        )
        ActionButton(
            icon = Icons.Default.Bolt,
            tint = boostOrange,
            background = Color(0xFFFBE9E7),
            size = 52.dp,
            onClick = onBoost
        )
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    tint: Color,
    background: Color,
    size: Dp,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(size)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(6.dp, CircleShape)
                .background(background, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(size * 0.45f)
            )
        }
    }
}

@Composable
private fun NoMoreProfilesCard() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(primaryContainer, secondaryContainer)
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                tint = primary,
                modifier = Modifier.size(48.dp)
            )
        }
        Text(
            "Has visto todos los perfiles!",
            color = onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            "Amplia tus filtros o vuelve mas tarde",
            color = onSurfaceVariant,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(50)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(listOf(pinkGradientStart, pinkGradientEnd)),
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 32.dp, vertical = 12.dp)
            ) {
                Text("Ajustar filtros", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
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
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            Triple("Explorar", Icons.Default.Explore, Icons.Outlined.Explore),
            Triple("Matches", Icons.Default.Favorite, Icons.Outlined.FavoriteBorder),
            Triple("Chats", Icons.Default.Chat, Icons.Outlined.Chat),
            Triple("Perfil", Icons.Default.Person, Icons.Outlined.Person)
        )

        items.forEachIndexed { index, (label, selectedIcon, unselectedIcon) ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = {
                    onTabSelected(index)
                    when (index) {
                        1 -> onMatchesClick()
                        2 -> onChatClick()
                        3 -> onProfileClick()
                    }
                },
                icon = {
                    Box {
                        Icon(
                            imageVector = if (selectedTab == index) selectedIcon else unselectedIcon,
                            contentDescription = label
                        )
                        if (index == 1 || index == 2) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(secondary, CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                            )
                        }
                    }
                },
                label = { Text(label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primary,
                    selectedTextColor = primary,
                    unselectedIconColor = onSurfaceVariant.copy(alpha = 0.5f),
                    unselectedTextColor = onSurfaceVariant.copy(alpha = 0.5f),
                    indicatorColor = primaryContainer
                )
            )
        }
    }
}