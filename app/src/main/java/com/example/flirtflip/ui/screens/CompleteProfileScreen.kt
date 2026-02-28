package com.example.flirtflip.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CompleteProfileScreen(
    onBackClick: () -> Unit = {}, // Función que se ejecuta al pulsar volver
    onCompleteSuccess: () -> Unit = {}
) {
    var bio by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val ageInt = age.toIntOrNull() ?: 0
    val isAdult = ageInt >= 18
    val isAgeEmpty = age.isEmpty()
    val showError = !isAgeEmpty && !isAdult

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFF6B9D), Color(0xFFFFC2D4))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Espacio extra para que el botón de volver no tape el título
            Spacer(modifier = Modifier.height(48.dp))

            Text("Casi listo...", fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Text("Cuéntanos más sobre ti", color = Color.White.copy(alpha = 0.8f))

            Spacer(modifier = Modifier.height(32.dp))

            // Selector de Foto
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Añadir Foto", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Campo Edad
            OutlinedTextField(
                value = age,
                onValueChange = {
                    if (it.all { char -> char.isDigit() } && it.length <= 2) {
                        age = it
                    }
                },
                label = { Text("Edad") },
                placeholder = { Text("Mínimo 18 años") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                isError = showError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White
                )
            )

            if (showError) {
                Text(
                    text = "Debes ser mayor de 18 para usar FlirtFlip",
                    color = Color.Yellow,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Bio
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Biografía") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Finalizar
            Button(
                onClick = {
                    val user = auth.currentUser
                    val userId = user?.uid

                    if (userId != null && isAdult) {
                        val updates = hashMapOf(
                            "age" to ageInt,
                            "bio" to bio,
                            "profileComplete" to true
                        )
                        db.collection("users").document(userId)
                            .update(updates as Map<String, Any>)
                            .addOnSuccessListener {
                                user.sendEmailVerification()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful){
                                            Toast.makeText(context, "Email de verificación enviado", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Error al enviar el email", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                //HOME
                                onCompleteSuccess() }
                            .addOnFailureListener {
                                Toast.makeText(context, "Error al guardar datos", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Verifica los datos", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = isAdult,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFFFF6B9D),
                    disabledContainerColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Finalizar Perfil", fontWeight = FontWeight.Bold)
            }
        }

        // --- BOTÓN DE VOLVER (Capas Superiores del Box) ---
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart),
            color = Color.White.copy(alpha = 0.3f),
            shape = RoundedCornerShape(12.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
        }
    }
}