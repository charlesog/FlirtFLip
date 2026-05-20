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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
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
import com.example.flirtflip.ui.viewmodel.SignUpViewModel

@Composable
fun SignUpScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
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
        // Circulos decorativos
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-60).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(secondary.copy(alpha = 0.15f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(50)
                )
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 60.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(primary.copy(alpha = 0.10f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(50)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.extraLarge)
                .padding(vertical = Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -40 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.flirtflip_sin_fondo),
                        contentDescription = "FlirtFlip Logo",
                        modifier = Modifier.size(160.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(Spacing.medium))

                    Text(
                        text = "Crear cuenta",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = primary
                    )
                    Text(
                        text = "Empieza tu aventura en FlirtFlip",
                        fontSize = 14.sp,
                        color = onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.extraLarge))

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
                            text = "Tus credenciales",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = onBackground
                        )

                        // Email
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

                        // Contrasena
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
                                        contentDescription = null,
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

                        // Confirmar contrasena
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirmar contrasena") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = primary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = onSurfaceVariant
                                    )
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            isError = confirmPassword.isNotEmpty() && confirmPassword != viewModel.password,
                            supportingText = {
                                if (confirmPassword.isNotEmpty() && confirmPassword != viewModel.password) {
                                    Text("Las contrasenas no coinciden", color = error)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primary,
                                unfocusedBorderColor = surfaceVariant,
                                focusedLabelColor = primary,
                                cursorColor = primary,
                                errorBorderColor = error
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Indicador seguridad contrasena
                        if (viewModel.password.isNotEmpty()) {
                            PasswordStrengthIndicator(password = viewModel.password)
                        }

                        // Error message
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

                        // Boton registro
                        val isFormValid = !viewModel.isLoading &&
                                viewModel.email.isNotBlank() &&
                                viewModel.password.isNotBlank() &&
                                confirmPassword == viewModel.password

                        Button(
                            onClick = { viewModel.onSignUpClick(onSuccess = onNavigateToProfile) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Dimensions.buttonHeight),
                            enabled = isFormValid,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        if (isFormValid) {
                                            Brush.horizontalGradient(
                                                colors = listOf(pinkGradientStart, pinkGradientEnd)
                                            )
                                        } else {
                                            Brush.horizontalGradient(
                                                colors = listOf(
                                                    primary.copy(alpha = 0.35f),
                                                    secondary.copy(alpha = 0.35f)
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
                                        text = "Crear cuenta",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        // Terminos
                        Text(
                            text = "Al registrarte aceptas nuestros Terminos de uso y Politica de privacidad",
                            color = onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.large))

            // Enlace login
            AnimatedVisibility(visible = visible, enter = fadeIn()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ya tienes cuenta?",
                        color = onSurfaceVariant,
                        fontSize = 14.sp
                    )
                    TextButton(onClick = onNavigateToLogin) {
                        Text(
                            text = "Inicia sesion",
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

@Composable
private fun PasswordStrengthIndicator(password: String) {
    val strength = when {
        password.length >= 8 &&
                password.any { it.isDigit() } &&
                password.any { it.isUpperCase() } &&
                password.any { !it.isLetterOrDigit() } -> 3

        password.length >= 8 &&
                (password.any { it.isDigit() } || password.any { it.isUpperCase() }) -> 2

        password.length >= 6 -> 1
        else -> 0
    }

    val (label, color) = when (strength) {
        3 -> "Fuerte" to Color(0xFF43A047)
        2 -> "Media" to Color(0xFFFF9800)
        1 -> "Debil" to error
        else -> "Muy debil" to error
    }

    Column {
        Text(
            text = "Seguridad: $label",
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (index < strength) color else surfaceVariant
                        )
                )
            }
        }
    }
}