package com.example.flirtflip.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.flirtflip.domain.model.DatePlan
import com.example.flirtflip.domain.model.DateRoulette
import com.example.flirtflip.domain.model.Hobby
import com.example.flirtflip.ui.theme.onSurfaceVariant
import com.example.flirtflip.ui.theme.primary

@Composable
fun DateRouletteDialog(
    myHobbies: List<Hobby>,
    theirHobbies: List<Hobby>,
    theirName: String,
    onSendPlan: (DatePlan) -> Unit,
    onDismiss: () -> Unit
) {
    val suggestions = remember { DateRoulette.suggest(myHobbies, theirHobbies) }
    var currentPlan by remember { mutableStateOf<DatePlan?>(null) }
    var isSpinning by remember { mutableStateOf(false) }
    var hasSpun by remember { mutableStateOf(false) }

    // Animación de rotación del icono dado
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            // Anima el dado girando rápido
            rotation.animateTo(
                targetValue = rotation.value + 720f,
                animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
            )
            // Resultado final
            currentPlan = suggestions.random()
            isSpinning = false
            hasSpun = true
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .background(Color.White, RoundedCornerShape(28.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // Header
                Text(
                    "🎲 Date Roulette",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "¿Qué hacéis tú y ${theirName}?",
                    fontSize = 14.sp,
                    color = onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Área principal - dado o resultado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.verticalGradient(listOf(Color(0xFFFFF0F7), Color(0xFFFFE4F0))),
                            RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = if (hasSpun) 2.dp else 1.dp,
                            color = if (hasSpun) primary else primary.copy(0.2f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (!hasSpun && !isSpinning) {
                        // Estado inicial - invita a girar
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Casino,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp).rotate(rotation.value),
                                tint = primary.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Pulsa para girar",
                                color = onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    } else if (isSpinning) {
                        // Girando
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Casino,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp).rotate(rotation.value),
                                tint = primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Eligiendo plan...", color = primary, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Resultado
                        currentPlan?.let { plan ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(plan.emoji, fontSize = 52.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    plan.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = primary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    plan.description,
                                    fontSize = 13.sp,
                                    color = onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botones
                if (!hasSpun) {
                    Button(
                        onClick = { isSpinning = true },
                        enabled = !isSpinning,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primary)
                    ) {
                        Icon(Icons.Default.Casino, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("¡Girar la ruleta!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                } else {
                    // Proponer plan
                    Button(
                        onClick = { currentPlan?.let { onSendPlan(it) } },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primary)
                    ) {
                        Icon(Icons.Default.Chat, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Proponer este plan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Volver a girar
                    OutlinedButton(
                        onClick = {
                            hasSpun = false
                            isSpinning = true
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = primary,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = primary.copy(0.4f)
                        )
                    ) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Otro plan", fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancelar", color = onSurfaceVariant)
                }
            }
        }
    }
}