package com.example.flirtflip.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flirtflip.ui.screens.SignUpScreen


sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object CompleteProfile : Screen("complete_profile")
    object Home : Screen("home")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "signup") {
        composable("signup") {
            SignUpScreen(
                onNavigateToProfile = {
                    navController.navigate("complete_profile") {
                        popUpTo("signup") { inclusive = true } // No volver atrás al registro
                    }
                }
            )
        }
        composable("complete_profile") {
            // Aquí irá la pantalla que hicimos para subir la foto y datos
            Text("¡Bienvenido! Ahora completa tu perfil")
        }
    }
}