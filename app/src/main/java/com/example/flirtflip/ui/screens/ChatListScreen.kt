package com.example.flirtflip.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.flirtflip.domain.model.Chat
import com.example.flirtflip.ui.theme.primary
import com.example.flirtflip.ui.viewmodel.ChatListViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onChatClick: (String) -> Unit,
    onBack: () -> Unit, // Recibe la acción para regresar a la HomeScreen
    viewModel: ChatListViewModel = hiltViewModel()
) {
    val chats by viewModel.chatSummaries.collectAsState()
    val currentUserId = viewModel.currentUserId

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mis Mensajes",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver atrás"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF5F8)
                )
            )
        },
        containerColor = Color(0xFFFFF5F8)
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
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
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var isLastMessageMine by remember { mutableStateOf<Boolean?>(null) }

    val userIds = chat.id.split("_")
    val otherUserId = userIds.firstOrNull { it != currentUserId } ?: ""

    LaunchedEffect(chat.id, otherUserId) {
        if (otherUserId.isNotBlank()) {
            try {
                val userDoc = db.collection("users").document(otherUserId).get().await()

                val profileMap = userDoc.get("profile") as? Map<*, *>

                imageUrl = profileMap?.get("mainPhotoUrl") as? String

                val name = profileMap?.get("name") as? String
                    ?: userDoc.getString("name")
                    ?: userDoc.getString("profile.name")

                realName = if (!name.isNullOrBlank()) name else "Usuario FlirtFlip"

                val messagesSnapshot = db.collection("chats")
                    .document(chat.id)
                    .collection("messages")
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .await()

                if (!messagesSnapshot.isEmpty) {
                    val lastMessageSender = messagesSnapshot.documents.first().getString("senderId")
                    isLastMessageMine = (lastMessageSender == currentUserId)
                }
            } catch (e: Exception) {
                realName = "Usuario FlirtFlip"
            }
        }
    }

    val timeText = remember(chat.lastMessageTimestamp) {
        chat.lastMessageTimestamp?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""
    }

    // Prefijos dinámicos basados en la consulta de base de datos
    val textToShow = when {
        chat.lastMessage.isNullOrBlank() -> "¡Has hecho un match! Di hola..."
        isLastMessageMine == true -> "Tú: ${chat.lastMessage}"
        isLastMessageMine == false -> "Él/Ella: ${chat.lastMessage}"
        else -> chat.lastMessage
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Contenedor del avatar de perfil (Imagen remota o inicial de respaldo)
        Box(
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFFF4081), Color(0xFFFF80AB))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Foto de perfil de $realName",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = realName.firstOrNull()?.toString()?.uppercase() ?: "?",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = realName,
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