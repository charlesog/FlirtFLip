package com.example.flirtflip.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.flirtflip.domain.model.Message
import com.example.flirtflip.ui.viewmodel.ChatDetailViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    onBack: () -> Unit,
    viewModel: ChatDetailViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    var textState by remember { mutableStateOf("") }
    var messageToDelete by remember { mutableStateOf<Message?>(null) }

    val db = remember { FirebaseFirestore.getInstance() }
    var matchName by remember { mutableStateOf("Cargando...") }
    var matchImageUrl by remember { mutableStateOf<String?>(null) }

    val userIds = viewModel.chatId.split("_")
    val otherUserId = userIds.firstOrNull { it != viewModel.currentUserId } ?: ""

    // Cargar dinámicamente el nombre y la foto de perfil desde Firestore
    LaunchedEffect(otherUserId) {
        if (otherUserId.isNotBlank()) {
            try {
                val userDoc = db.collection("users").document(otherUserId).get().await()

                val profileMap = userDoc.get("profile") as? Map<*, *>

                matchImageUrl = profileMap?.get("mainPhotoUrl") as? String

                val name = profileMap?.get("name") as? String
                    ?: userDoc.getString("name")
                    ?: userDoc.getString("profile.name")

                matchName = if (!name.isNullOrBlank()) name else "Usuario FlirtFlip"
            } catch (e: Exception) {
                matchName = "Usuario FlirtFlip"
            }
        }
    }

    if (messageToDelete != null) {
        AlertDialog(
            onDismissRequest = { messageToDelete = null },
            title = { Text("¿Eliminar tu mensaje?") },
            text = { Text("Esta acción eliminará el mensaje de la conversación de forma definitiva.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        messageToDelete?.let { viewModel.deleteMessage(it.id) }
                        messageToDelete = null
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { messageToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Imagen de perfil con Coil o iniciales si no existe URL
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(colors = listOf(Color(0xFFFF4081), Color(0xFFFF80AB)))),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!matchImageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = matchImageUrl,
                                    contentDescription = "Foto de $matchName",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = matchName.firstOrNull()?.toString()?.uppercase() ?: "?",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = matchName, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF4CAF50)))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Conexión activa", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFF5F8))
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = textState,
                        onValueChange = { textState = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escribe un mensaje...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (textState.isNotBlank()) {
                                viewModel.sendTextMessage(textState)
                                textState = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Brush.linearGradient(colors = listOf(Color(0xFFFF4081), Color(0xFFFF80AB))), shape = CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Enviar", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        },
        containerColor = Color(0xFFFFF5F8)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            items(messages) { message ->
                ChatBubble(
                    message = message,
                    isMine = message.senderId == viewModel.currentUserId,
                    onLongClick = { messageToDelete = message }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(message: Message, isMine: Boolean, onLongClick: () -> Unit) {
    val alignment = if (isMine) Alignment.End else Alignment.Start
    val color = if (isMine) Color(0xFFFF4081) else Color(0xFFEFEFEF)
    val contentColor = if (isMine) Color.White else Color(0xFF2C2C2C)
    val shape = if (isMine) RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp) else RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp)

    val timeText = remember(message.timestamp) {
        message.timestamp.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = color,
            shape = shape,
            tonalElevation = 1.dp,
            modifier = Modifier
                .widthIn(max = 265.dp)
                .combinedClickable(
                    onLongClick = { if (isMine) onLongClick() },
                    onClick = {}
                )
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                Text(
                    text = message.text,
                    color = contentColor,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = timeText,
                        color = if (isMine) Color.White.copy(alpha = 0.7f) else Color.Gray,
                        fontSize = 10.sp
                    )
                    if (isMine) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Estado de lectura",
                            // Azul eléctrico clásico de lectura
                            tint = if (message.isRead) Color(0xFF00B0FF) else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}