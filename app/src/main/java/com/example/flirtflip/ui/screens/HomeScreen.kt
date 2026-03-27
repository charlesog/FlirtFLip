package com.example.flirtflip.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Colores del tema ───────────────────────────────────────────────────────
private val PinkPrimary   = Color(0xFFFF6B9D)
private val PinkLight     = Color(0xFFFFC2D4)
private val PinkDark      = Color(0xFFE91E63)
private val BackgroundApp = Color(0xFFFFFBFF)

// ─── Modelo de pestaña ──────────────────────────────────────────────────────
private data class BottomTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int = 0
)

private val tabs = listOf(
    BottomTab("discover",  "Descubrir", Icons.Filled.Favorite,         Icons.Filled.FavoriteBorder),
    BottomTab("matches",   "Matches",   Icons.Filled.People,           Icons.Filled.PeopleOutline,       badgeCount = 3),
    BottomTab("messages",  "Mensajes",  Icons.Filled.ChatBubble,       Icons.Filled.ChatBubbleOutline,   badgeCount = 5),
    BottomTab("activity",  "Actividad", Icons.Filled.Notifications,    Icons.Filled.NotificationsNone),
    BottomTab("profile",   "Perfil",    Icons.Filled.Person,           Icons.Filled.PersonOutline)
)

// ─── Pantalla principal ─────────────────────────────────────────────────────
@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = BackgroundApp,
        bottomBar = {
            FlirtFlipBottomBar(
                tabs = tabs,
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DiscoverTabPlaceholder()
                1 -> MatchesTabPlaceholder()
                2 -> MessagesTabPlaceholder()
                3 -> ActivityTabPlaceholder()
                4 -> ProfileTabPlaceholder()
            }
        }
    }
}

// ─── Bottom Bar ─────────────────────────────────────────────────────────────
@Composable
private fun FlirtFlipBottomBar(
    tabs: List<BottomTab>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 12.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                FlirtFlipTabItem(
                    tab = tab,
                    selected = index == selectedIndex,
                    onClick = { onTabSelected(index) }
                )
            }
        }
    }
}

@Composable
private fun FlirtFlipTabItem(
    tab: BottomTab,
    selected: Boolean,
    onClick: () -> Unit
) {
    val iconColor  = if (selected) PinkPrimary else Color(0xFFBDBDBD)
    val labelColor = if (selected) PinkPrimary else Color(0xFFBDBDBD)

    IconButton(
        onClick = onClick,
        modifier = Modifier.size(60.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BadgedBox(
                badge = {
                    if (tab.badgeCount > 0 && !selected) {
                        Badge(
                            containerColor = PinkDark,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = if (tab.badgeCount > 9) "9+" else tab.badgeCount.toString(),
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                    contentDescription = tab.label,
                    tint = iconColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = tab.label,
                fontSize = 10.sp,
                color = labelColor,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

// ─── Placeholders de cada pestaña ───────────────────────────────────────────

@Composable
private fun DiscoverTabPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.verticalGradient(listOf(PinkPrimary, PinkLight))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Descubrir", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PinkPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Aquí irán las tarjetas\nde perfiles para deslizar",
                fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MatchesTabPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Matches", fontSize = 26.sp, fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1B16), modifier = Modifier.padding(vertical = 8.dp))
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(PinkLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.People, null, tint = PinkPrimary, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Tus matches aparecerán aquí", fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun MessagesTabPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Mensajes", fontSize = 26.sp, fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1B16), modifier = Modifier.padding(vertical = 8.dp))
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(PinkLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ChatBubble, null, tint = PinkPrimary, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Tus conversaciones\naparecerán aquí", fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun ActivityTabPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Actividad", fontSize = 26.sp, fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1B16), modifier = Modifier.padding(vertical = 8.dp))
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(PinkLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Notifications, null, tint = PinkPrimary, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Quién te ha dado like,\nvisitas y más", fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun ProfileTabPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Mi Perfil", fontSize = 26.sp, fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1B16), modifier = Modifier.padding(vertical = 8.dp))
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(100.dp).clip(CircleShape)
                        .background(Brush.verticalGradient(listOf(PinkPrimary, PinkLight))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, null, tint = Color.White, modifier = Modifier.size(56.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Tu perfil aparecerá aquí", fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = {},
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Icon(Icons.Filled.Edit, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Editar perfil")
                }
            }
        }
    }
}