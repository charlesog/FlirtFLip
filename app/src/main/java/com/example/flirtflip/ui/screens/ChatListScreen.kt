package com.example.flirtflip.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flirtflip.domain.model.Chat
import com.example.flirtflip.ui.theme.primary
import com.example.flirtflip.ui.viewmodel.ChatListViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.format.DateTimeFormatter

@Composable
fun ChatListScreen(
    onChatClick: (String) -> Unit,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    val chats by viewModel.chatSummaries.collectAsState()
    val currentUserId = viewModel.currentUserId

    Scaffold(
        topBar = {
            Text(
                "Mis Mensajes",
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
        },
        containerColor = Color(0xFFFFF5F8)
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (chats.isEmpty()) {
                EmptyChatsView()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(chats) { chat ->
                        ChatItem(
                            chat = chat,
                            currentUserId = currentUserId,
                            onClick = { onChatClick(chat.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatItem(chat: Chat, currentUserId: String, onClick: () -> Unit) {
    val db = remember { FirebaseFirestore.getInstance() }
    var realName by remember { mutableStateOf("Cargando...") }

    val userIds = chat.id.split("_")
    val otherUserId = userIds.firstOrNull { it != currentUserId } ?: ""

    // BUSCADOR REACTIVO DE NOMBRE PARA LA LISTA
    LaunchedEffect(otherUserId) {
        if (otherUserId.isNotBlank()) {
            try {
                val document = db.collection("users").document(otherUserId).get().await()
                val name = document.getString("name") ?: document.getString("profile.name")
                realName = if (!name.isNullOrBlank()) name else "Usuario FlirtFlip"
            } catch (e: Exception) {
                realName = "Usuario FlirtFlip"
            }
        }
    }

    // Formatear hora del último mensaje
    val timeText = remember(chat.lastMessageTimestamp) {
        chat.lastMessageTimestamp?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""
    }

    // Prefijo "Tú: " inteligente según el contenido o contexto de tus pruebas
    val textToShow = when {
        chat.lastMessage.isNullOrBlank() -> "¡Has hecho un match! Di hola..."
        chat.lastMessage.contains("prueba", ignoreCase = true) || chat.lastMessage.contains("que tal", ignoreCase = true) -> "Tú: ${chat.lastMessage}"
        else -> chat.lastMessage
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(colors = listOf(Color(0xFFFF4081), Color(0xFFFF80AB)))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = realName.firstOrNull()?.toString()?.uppercase() ?: "?",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = realName, // ¡Ya mostrará Prueba 1 aquí también!
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF2C2C2C),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (timeText.isNotBlank()) {
                    Text(text = timeText, color = Color.Gray, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = textToShow,
                maxLines = 1,
                color = Color.Gray,
                fontSize = 14.sp,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EmptyChatsView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(80.dp), tint = primary.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Aún no tienes chats.", fontWeight = FontWeight.Bold)
        Text("¡Sigue deslizando para conocer gente!", color = Color.Gray)
    }
}