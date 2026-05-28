package com.example.flirtflip.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flirtflip.ui.theme.onBackground
import com.example.flirtflip.ui.theme.onSurfaceVariant
import com.example.flirtflip.ui.theme.primary
import com.example.flirtflip.ui.viewmodel.WelcomeBotViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class BotMessage(
    val text: String,
    val isBot: Boolean = true,
    val options: List<String> = emptyList()
)

private val welcomeFlow: List<BotMessage> = listOf(
    BotMessage("¡Hola! 👋 Soy FlirtBot, tu guía en FlirtFlip. ¿Listo para encontrar a alguien especial? 💕"),
    BotMessage(
        "Te voy a enseñar cómo funciona la app en segundos. ¿Por dónde empezamos?",
        options = listOf(
            "🔥 Explorar perfiles",
            "💬 Chats y matches",
            "👤 Mi perfil",
            "⚙️ Preferencias"
        )
    ),
)

private fun getBotReply(option: String): List<BotMessage> = when {
    option.contains("Explorar") -> listOf(
        BotMessage("¡La pestaña Explorar es el corazón de FlirtFlip! 🧭"),
        BotMessage("Verás tarjetas con perfiles de personas cerca de ti. Puedes:\n\n❌ Pulsar la X para pasar\n💚 Pulsar el corazón para dar like\n⭐ La estrella es un Super Like\n⚡ El rayo es un Boost para aumentar visibilidad"),
        BotMessage(
            "Si a alguien le gustas también, ¡es un Match! 🎉 Te avisará al instante.",
            options = listOf("💬 Ver Chats", "👤 Ver Perfil", "⚙️ Ver Preferencias", "✅ ¡Entendido!")
        )
    )

    option.contains("Chat") || option.contains("match", ignoreCase = true) -> listOf(
        BotMessage("¡Los Matches son conexiones mutuas! ❤️"),
        BotMessage("Cuando hay match, aparece en la pestaña Matches. Desde ahí puedes:\n\n• Ver su foto y nombre\n• Pulsar 'Enviar mensaje' para iniciar la conversación\n• Tocar la tarjeta para abrir el chat directamente"),
        BotMessage(
            "En la pestaña Chats verás todos tus mensajes activos ordenados por el más reciente. 💬",
            options = listOf(
                "🔥 Ver Explorar",
                "👤 Ver Perfil",
                "⚙️ Ver Preferencias",
                "✅ ¡Entendido!"
            )
        )
    )

    option.contains("perfil", ignoreCase = true) || option.contains("Perfil") -> listOf(
        BotMessage("Tu perfil es tu carta de presentación 📸"),
        BotMessage("En la pestaña Perfil puedes:\n\n• Ver cómo te ven los demás\n• Pulsar 'Editar perfil' para cambiar tu foto, bio, trabajo e intereses\n• Añadir fotos a tu galería\n• Elegir tus hobbies para matches más afines"),
        BotMessage(
            "💡 Un perfil completo con buena foto multiplica tus posibilidades de match.",
            options = listOf(
                "🔥 Ver Explorar",
                "💬 Ver Chats",
                "⚙️ Ver Preferencias",
                "✅ ¡Entendido!"
            )
        )
    )

    option.contains("Prefer") || option.contains("⚙️") -> listOf(
        BotMessage("Las Preferencias te dan control total ⚙️"),
        BotMessage("Accede pulsando el icono ≡ en la barra superior de Explorar. Desde ahí:\n\n• 🎂 Filtra por rango de edad\n• 🌙 Cambia entre tema claro, oscuro o automático\n• 📋 Lee la información legal\n• 🚪 Cierra sesión de forma segura"),
        BotMessage(
            "Las notificaciones 🔔 te avisarán cuando tengas nuevos matches o mensajes.",
            options = listOf(
                "🔥 Ver Explorar",
                "💬 Ver Chats",
                "👤 Ver Perfil",
                "🎉 ¡Empezar a explorar!"
            )
        )
    )

    option.contains("Entendido") || option.contains("gracias", ignoreCase = true) -> listOf(
        BotMessage("¡Perfecto! Ya sabes todo lo que necesitas 🚀"),
        BotMessage(
            "Recuerda: completa tu perfil, sé tú mismo/a y ¡que empiece el match! 💕",
            options = listOf(
                "🔥 Ver Explorar",
                "💬 Ver Chats",
                "👤 Ver Perfil",
                "⚙️ Ver Preferencias",
                "🎉 ¡Empezar a explorar!"
            )
        )
    )

    option.contains("Empezar") || option.contains("explorar") -> listOf(
        BotMessage("¡Vamos allá! Te espero en la app 💫 Suerte encontrando a alguien especial 🌹")
    )

    else -> listOf(
        BotMessage(
            "¡Claro! ¿En qué más puedo ayudarte?",
            options = listOf(
                "🔥 Explorar perfiles",
                "💬 Chats y matches",
                "👤 Mi perfil",
                "⚙️ Preferencias"
            )
        )
    )
}

@Composable
fun WelcomeBotScreen(
    onFinish: () -> Unit,
    viewModel: WelcomeBotViewModel = hiltViewModel()
) {
    val messages = remember { mutableStateListOf<BotMessage>() }
    val listState = rememberLazyListState()
    var isTyping by remember { mutableStateOf(false) }
    var canFinish by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Función para marcar visto y salir
    val finishAndMark: () -> Unit = {
        scope.launch {
            viewModel.markWelcomeSeen()
            onFinish()
        }
    }

    // Mensajes iniciales con delay
    LaunchedEffect(Unit) {
        for (msg in welcomeFlow) {
            isTyping = true
            delay(900)
            isTyping = false
            messages.add(msg)
            delay(300)
        }
    }

    // Auto-scroll al último mensaje
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF0F7), Color(0xFFFFE4F0))))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header — respeta la barra de estado ───────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = statusBarPadding.calculateTopPadding())
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                Brush.radialGradient(listOf(Color(0xFFFF4081), Color(0xFFFF80AB))),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "FlirtBot",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = primary
                        )
                        Text("Guía de FlirtFlip", fontSize = 12.sp, color = onSurfaceVariant)
                    }
                }
            }

            // ── Chat ──────────────────────────────────────────────────────
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(messages) { msg ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 2 }
                    ) {
                        Column {
                            if (msg.isBot) {
                                BotBubble(text = msg.text)
                                if (msg.options.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    OptionsRow(
                                        options = msg.options,
                                        onSelect = { selected ->
                                            if (selected.contains("Empezar") || selected.contains("explorar")) {
                                                canFinish = true
                                            }
                                            // Quitar opciones del mensaje actual
                                            val lastIdx =
                                                messages.indexOfLast { it.options.isNotEmpty() }
                                            if (lastIdx >= 0) {
                                                messages[lastIdx] =
                                                    messages[lastIdx].copy(options = emptyList())
                                            }
                                            // Respuesta del usuario
                                            messages.add(BotMessage(selected, isBot = false))
                                            // Respuestas del bot con delay
                                            scope.launch {
                                                val replies = getBotReply(selected)
                                                for (reply in replies) {
                                                    isTyping = true
                                                    delay(800)
                                                    isTyping = false
                                                    messages.add(reply)
                                                    delay(200)
                                                }
                                                // Si el último mensaje lleva al final → salir automáticamente
                                                if (replies.lastOrNull()?.text?.contains("allá") == true) {
                                                    delay(1200)
                                                    finishAndMark()
                                                }
                                            }
                                        }
                                    )
                                }
                            } else {
                                UserBubble(text = msg.text)
                            }
                        }
                    }
                }
                if (isTyping) {
                    item { TypingIndicator() }
                }
            }

            // ── Botón saltar / continuar ──────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(bottom = navigationBarPadding.calculateBottomPadding())
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Button(
                    onClick = finishAndMark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canFinish) primary else Color(0xFFEEEEEE)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (canFinish) "¡Empezar a explorar! 🚀" else "Saltar introducción",
                        fontWeight = FontWeight.Bold,
                        color = if (canFinish) Color.White else onSurfaceVariant
                    )
                    if (canFinish) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.ArrowForward,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Componentes de chat ───────────────────────────────────────────────────────

@Composable
private fun BotBubble(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    Brush.radialGradient(listOf(Color(0xFFFF4081), Color(0xFFFF80AB))),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Surface(
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 18.dp,
                bottomStart = 18.dp,
                bottomEnd = 18.dp
            ),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.widthIn(max = 270.dp)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                fontSize = 14.sp,
                color = onBackground,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun UserBubble(text: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 4.dp,
                bottomStart = 18.dp,
                bottomEnd = 18.dp
            ),
            color = primary,
            modifier = Modifier.widthIn(max = 240.dp)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun OptionsRow(options: List<String>, onSelect: (String) -> Unit) {
    Column(
        modifier = Modifier.padding(start = 46.dp, end = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEach { option ->
            OutlinedButton(
                onClick = { onSelect(option) },
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, primary),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(option, fontSize = 13.sp, color = primary, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    Brush.radialGradient(listOf(Color(0xFFFF4081), Color(0xFFFF80AB))),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 18.dp,
                bottomStart = 18.dp,
                bottomEnd = 18.dp
            ),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { i ->
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        delay(i * 200L)
                        while (true) {
                            visible = true; delay(600)
                            visible = false; delay(600)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(
                                if (visible) primary else primary.copy(alpha = 0.25f),
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}