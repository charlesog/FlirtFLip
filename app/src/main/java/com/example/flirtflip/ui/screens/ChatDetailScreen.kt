package com.example.flirtflip.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.flirtflip.domain.model.Message
import com.example.flirtflip.ui.viewmodel.ChatDetailViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
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
    var showDateRoulette by remember { mutableStateOf(false) }

    val db = remember { FirebaseFirestore.getInstance() }
    var matchName by remember { mutableStateOf("Cargando...") }
    var matchImageUrl by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val userIds = viewModel.chatId.split("_")
    val otherUserId = userIds.firstOrNull { it != viewModel.currentUserId } ?: ""

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

    // Scroll al último mensaje cuando llegan nuevos
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    // Dialog Date Roulette
    if (showDateRoulette) {
        DateRouletteDialog(
            myHobbies = emptyList(),
            theirHobbies = emptyList(),
            theirName = matchName,
            onSendPlan = { plan ->
                viewModel.sendDateRouletteMessage(plan.emoji, plan.title, plan.description)
                showDateRoulette = false
            },
            onDismiss = { showDateRoulette = false }
        )
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
                            Text(
                                text = matchName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CAF50))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Conexión activa", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón Date Roulette en la barra superior
                    IconButton(onClick = { showDateRoulette = true }) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Date Roulette",
                            tint = Color(0xFFFF4081)
                        )
                    }
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
                    // Botón Date Roulette inline (dado) en la barra de input
                    IconButton(
                        onClick = { showDateRoulette = true },
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                Brush.linearGradient(colors = listOf(Color(0xFFFF6B9D), Color(0xFFFF4081))),
                                shape = CircleShape
                            )
                    ) {
                        Text("🎲", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
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
                            .background(
                                Brush.linearGradient(colors = listOf(Color(0xFFFF4081), Color(0xFFFF80AB))),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Enviar",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFFFFF5F8)
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(messages) { message ->
                if (message.text.startsWith("🎲DATE_ROULETTE:")) {
                    DateRouletteBubble(
                        message = message,
                        isMine = message.senderId == viewModel.currentUserId,
                        theirName = matchName,
                        onLongClick = { messageToDelete = message }
                    )
                } else {
                    ChatBubble(
                        message = message,
                        isMine = message.senderId == viewModel.currentUserId,
                        onLongClick = { messageToDelete = message }
                    )
                }
            }
        }
    }
}

/**
 * Burbuja especial para mensajes de Date Roulette.
 * Muestra el plan de forma visual y atractiva.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DateRouletteBubble(
    message: Message,
    isMine: Boolean,
    theirName: String,
    onLongClick: () -> Unit
) {
    val alignment = if (isMine) Alignment.End else Alignment.Start

    // Parsear el contenido: "🎲DATE_ROULETTE:emoji|titulo|descripcion"
    val content = message.text.removePrefix("🎲DATE_ROULETTE:")
    val parts = content.split("|")
    val emoji = parts.getOrElse(0) { "🎲" }
    val title = parts.getOrElse(1) { "Propuesta de cita" }
    val description = parts.getOrElse(2) { "" }

    val timeText = remember(message.timestamp) {
        message.timestamp.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .combinedClickable(
                    onLongClick = { if (isMine) onLongClick() },
                    onClick = {}
                )
        ) {
            Card(
                shape = if (isMine)
                    RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
                else
                    RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = if (isMine)
                                    listOf(Color(0xFFFF4081), Color(0xFFFF6B9D), Color(0xFFFF80AB))
                                else
                                    listOf(Color(0xFF7C4DFF), Color(0xFF9C27B0), Color(0xFFBA68C8))
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🎲", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isMine) "Tu propuesta de cita" else "Propuesta de cita de $theirName",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Emoji grande
                        Text(text = emoji, fontSize = 44.sp, textAlign = TextAlign.Center)

                        Spacer(modifier = Modifier.height(6.dp))

                        // Título
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        if (description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = description,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Timestamp
                        Text(
                            text = timeText,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
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
                            tint = if (message.isRead) Color(0xFF00B0FF) else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}