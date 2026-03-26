package com.example.flirtflip.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flirtflip.ui.viewmodel.SignUpViewModel
import com.example.flirtflip.ui.viewmodel.SignUpUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onBackClick: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {},
    viewModel: SignUpViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var firstSurname by remember { mutableStateOf("") }
    var secondSurname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var acceptedTerms by remember { mutableStateOf(false) }
    var acceptedAge by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()

    // Observar el estado del ViewModel
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is SignUpUiState.Success -> {
                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                onSignUpSuccess()
                viewModel.resetState()
            }
            is SignUpUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> { /* No hacer nada */ }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFF6B9D),
                    Color(0xFFFF8FB3), Color(0xFFFFC2D4))
            )
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Text(text = "Crear Cuenta", fontSize = 36.sp, fontWeight = FontWeight.Bold,
                color = Color.White)
            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = firstSurname,
                onValueChange = { firstSurname = it},
                label = { Text("Primer Apellido") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = secondSurname,
                onValueChange = { secondSurname = it},
                label = { Text("Segundo Apellido") },
                modifier = Modifier.fillMaxSize(),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = if (passwordVisible) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña"
                            else "Mostrar contraseña", tint = Color(0xFFFF6B9D))
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )


            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = acceptedTerms,
                    onCheckedChange = { acceptedTerms = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.White,
                        checkmarkColor = Color(0xFFFF6B9D),
                        uncheckedColor = Color.White
                    )
                )
                Text(
                    text = "Acepto los Términos y Condiciones y la Política de Privacidad",
                    color = Color.White,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = acceptedAge,
                    onCheckedChange = { acceptedAge = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.White,
                        checkmarkColor = Color(0xFFFF6B9D),
                        uncheckedColor = Color.White
                    )
                )
                Text(
                    text = "Confirmo que tengo un minimo de 18 años",
                    color = Color.White,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN REGISTRARSE (Se habilita solo si acceptedTerms and acceptedAge es true)
            Button(
                onClick = {
                    viewModel.signUp(
                        name = name,
                        firstSurname = firstSurname,
                        secondSurname = secondSurname,
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        acceptedTerms = acceptedTerms,
                        acceptedAge = acceptedAge
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = acceptedTerms && acceptedAge && 
                          name.isNotBlank() && 
                          firstSurname.isNotBlank() && 
                          email.isNotBlank() && 
                          password.isNotBlank() &&
                          confirmPassword.isNotBlank() &&
                          uiState !is SignUpUiState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFFFF6B9D),
                    disabledContainerColor = Color.White.copy(alpha = 0.5f),
                    disabledContentColor = Color.Gray
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (uiState is SignUpUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFFFF6B9D)
                    )
                } else {
                    Text(text = "Registrarse", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        IconButton(onClick = onBackClick, modifier = Modifier.padding(16.dp).align(Alignment.TopStart)) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
        }
    }
}