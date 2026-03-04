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
import kotlinx.coroutines.delay // IMPORTANTE: Para que delay funcione

@Composable
fun CompleteProfileScreen(
    onBackClick: () -> Unit = {},
    onCompleteSuccess: () -> Unit = {}
) {
    var bio by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedGender by remember { mutableStateOf("") }

    val isAdult = remember(age) {
        val ageInt = age.toIntOrNull() ?: 0
        ageInt >= 18
    }

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
            Spacer(modifier = Modifier.height(48.dp))

            Text("Casi listo...", fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Text("Cuéntanos más sobre ti", color = Color.White.copy(alpha = 0.8f))

            Spacer(modifier = Modifier.height(32.dp))

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

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Sobre mí") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = age,
                onValueChange = {
                    if (it.all { char -> char.isDigit() } && it.length <= 2) {
                        age = it
                    }
                },
                label = { Text("Edad") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                isError = age.isNotEmpty() && !isAdult,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            LaunchedEffect(age) {
                if (age.isNotEmpty() && !isAdult) {
                    delay(2000)
                    Toast.makeText(
                        context, "Debes ser mayor de 18 años",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Selecciona tu Género", color = Color.White, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val options = listOf("Hombre", "Mujer", "Otro") //Maybe campo de que eres (therian, elle, etc.)
                    options.forEach { option ->
                        val isSelected = selectedGender == option
                        Button(
                            onClick = { selectedGender = option },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFFFF5A60) else Color.White.copy(alpha = 0.9f),
                                contentColor = if (isSelected) Color.White else Color.DarkGray
                            )
                        ) {
                            Text(option, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val userId = auth.currentUser?.uid
                    if (userId != null && isAdult && bio.isNotBlank() && selectedGender.isNotBlank()) {
                        val updates = hashMapOf(
                            "bio" to bio,
                            "age" to age,
                            "gender" to selectedGender,
                            "profileComplete" to true
                        )
                        db.collection("users").document(userId)
                            .update(updates as Map<String, Any>)
                            .addOnSuccessListener {
                                auth.currentUser?.sendEmailVerification()
                                onCompleteSuccess()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = isAdult && bio.isNotEmpty(), // El botón solo se activa si es adulto
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

        IconButton(
            onClick = onBackClick,
            modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
        }
    }
}