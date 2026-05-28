package com.example.flirtflip.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flirtflip.R
import com.example.flirtflip.ui.theme.Dimensions
import com.example.flirtflip.ui.theme.Spacing
import com.example.flirtflip.ui.theme.error
import com.example.flirtflip.ui.theme.errorContainer
import com.example.flirtflip.ui.theme.onBackground
import com.example.flirtflip.ui.theme.onSurfaceVariant
import com.example.flirtflip.ui.theme.pinkGradientEnd
import com.example.flirtflip.ui.theme.pinkGradientStart
import com.example.flirtflip.ui.theme.primary
import com.example.flirtflip.ui.theme.secondary
import com.example.flirtflip.ui.theme.surfaceVariant
import com.example.flirtflip.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onNavigateToHome: (showWelcome: Boolean) -> Unit,
    onNavigateToSignUp: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF0F7),
                        Color(0xFFFFE0EF),
                        Color(0xFFFFD6EC)
                    )
                )
            )
    ) {
        // Circulos decorativos suaves
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-100).dp, y = (-80).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primary.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(50)
                )
        )
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            secondary.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(50)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.extraLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo y titulo
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -40 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.flirtflip_sin_fondo),
                        contentDescription = "FlirtFlip Logo",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "Encuentra tu conexion perfecta",
                        fontSize = 14.sp,
                        color = onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Card formulario
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 60 })
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.large),
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        Text(
                            text = "Iniciar sesion",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = onBackground
                        )

                        // Campo email
                        OutlinedTextField(
                            value = viewModel.email,
                            onValueChange = { viewModel.email = it },
                            label = { Text("Email") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = primary)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primary,
                                unfocusedBorderColor = surfaceVariant,
                                focusedLabelColor = primary,
                                cursorColor = primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Campo contrasena
                        OutlinedTextField(
                            value = viewModel.password,
                            onValueChange = { viewModel.password = it },
                            label = { Text("Contrasena") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = primary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                                        tint = onSurfaceVariant
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primary,
                                unfocusedBorderColor = surfaceVariant,
                                focusedLabelColor = primary,
                                cursorColor = primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Fila: Recuérdame + ¿Olvidaste la contraseña?
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = viewModel.rememberMe,
                                    onCheckedChange = { viewModel.rememberMe = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = primary,
                                        uncheckedColor = onSurfaceVariant
                                    )
                                )
                                Text(
                                    text = "Recuérdame",
                                    fontSize = 13.sp,
                                    color = onSurfaceVariant,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                            TextButton(onClick = { }) {
                                Text(
                                    text = "¿Olvidaste tu contraseña?",
                                    color = primary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Error
                        AnimatedVisibility(visible = viewModel.errorMessage != null) {
                            viewModel.errorMessage?.let { msg ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = errorContainer),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = msg,
                                        color = error,
                                        modifier = Modifier.padding(Spacing.small),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // Boton login
                        Button(
                            onClick = {
                                viewModel.onLoginClick(onSuccess = { showWelcome ->
                                    onNavigateToHome(
                                        showWelcome
                                    )
                                })
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Dimensions.buttonHeight),
                            enabled = !viewModel.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        if (!viewModel.isLoading) {
                                            Brush.horizontalGradient(
                                                colors = listOf(pinkGradientStart, pinkGradientEnd)
                                            )
                                        } else {
                                            Brush.horizontalGradient(
                                                colors = listOf(
                                                    primary.copy(alpha = 0.4f),
                                                    secondary.copy(alpha = 0.4f)
                                                )
                                            )
                                        },
                                        RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (viewModel.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "Iniciar sesion",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.large))

            // Enlace registro
            AnimatedVisibility(visible = visible, enter = fadeIn()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Aun no tienes cuenta?",
                        color = onSurfaceVariant,
                        fontSize = 14.sp
                    )
                    TextButton(onClick = onNavigateToSignUp) {
                        Text(
                            text = "Registrate",
                            color = primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}