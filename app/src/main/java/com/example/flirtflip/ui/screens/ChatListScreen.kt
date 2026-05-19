package com.example.flirtflip.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flirtflip.domain.model.Chat
import com.example.flirtflip.ui.theme.primary
import com.example.flirtflip.ui.viewmodel.ChatListViewModel

@Composable
fun ChatListScreen(
    onChatClick: (String) -> Unit,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    val chats by viewModel.chatSummaries.collectAsState()

    Scaffold(
        topBar = {
            Text(
                "Mis Mensajes",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (chats.isEmpty()) {
                EmptyChatsView()
            } else {
                LazyColumn {
                    items(chats) { chat ->
                        ChatItem(chat = chat, onClick = { onChatClick(chat.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun ChatItem(chat: Chat, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Círculo para la foto (puedes usar AsyncImage de Coil después)
        Surface(
            modifier = Modifier.size(55.dp).clip(CircleShape),
            color = Color.LightGray.copy(alpha = 0.3f)
        ) {
            // Aquí iría la imagen del usuario
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chat.otherUserName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = chat.lastMessage ?: "¡Has hecho un match! Di hola...",
                maxLines = 1,
                color = Color.Gray,
                fontSize = 14.sp
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
        Icon(
            Icons.Default.ChatBubbleOutline,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Aún no tienes chats.", fontWeight = FontWeight.Bold)
        Text("¡Sigue deslizando para conocer gente!", color = Color.Gray)
    }
}