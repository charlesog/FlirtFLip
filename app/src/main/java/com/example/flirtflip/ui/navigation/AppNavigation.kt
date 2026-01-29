package com.example.flirtflip.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flirtflip.ui.screens.LoginFormScreen
import com.example.flirtflip.ui.screens.LoginScreen
import com.example.flirtflip.ui.screens.SignUpScreen

// Definición de rutas
sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home") // Esta será tu pantalla principal después del login
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        // Pantalla de bienvenida (pantalla inicial)
        composable(Screen.Welcome.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        // Pantalla de inicio de sesión
        composable(Screen.Login.route) {
            LoginFormScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    // TODO: Verificar credenciales
                    // Navegar a la pantalla principal
                    navController.navigate(Screen.Home.route) {
                        // Limpiar el back stack para que no pueda volver al login
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de registro
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    // TODO: Crear usuario
                    // Navegar a la pantalla principal
                    navController.navigate(Screen.Home.route) {
                        // Limpiar el back stack para que no pueda volver al registro
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla principal (implementar tu contenido aquí)
        composable(Screen.Home.route) {
            // TODO: Implementar la pantalla principal de la app
            // Por ahora, una pantalla temporal
            HomeScreenPlaceholder()
        }
    }
}

@Composable
fun HomeScreenPlaceholder() {
    Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = "¡Bienvenido a FlirtFlip! 💘\n\nAquí irá tu pantalla principal",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontSize = 20.sp
        )
    }
}
