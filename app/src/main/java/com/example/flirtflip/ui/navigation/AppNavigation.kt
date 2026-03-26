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
import com.example.flirtflip.ui.screens.CompleteProfileScreen

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

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        // Pantalla de bienvenida
        composable(Screen.Welcome.route) {
            LoginScreen(
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onSignUpClick = { navController.navigate(Screen.SignUp.route) }
            )
        }

        // Pantalla de inicio de sesión
        composable(Screen.Login.route) {
            LoginFormScreen(
                onBackClick = { navController.popBackStack() },
                onLoginSuccess = {
                    // Quitamos el popUpTo de aquí para permitir volver atrás si es necesario
                    navController.navigate(Screen.CompleteProfile.route)
                }
            )
        }

        // Pantalla de registro
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onSignUpSuccess = {
                    // Quitamos el popUpTo de aquí también para mantener la pila
                    navController.navigate(Screen.CompleteProfile.route)
                }
            )
        }

        // Pantalla: Completar Perfil
        composable(Screen.CompleteProfile.route) {
            CompleteProfileScreen(
                // Ahora popBackStack funcionará porque hay pantallas debajo en la pila
                onBackClick = { navController.popBackStack() },
                onCompleteSuccess = {
                    // Al terminar con éxito, aquí sí limpiamos TODO para ir al Home
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla principal
        composable(Screen.Home.route) {
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
            text = "¡Perfil Completado! 💘\n\nBienvenido al Home de FlirtFlip",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontSize = 20.sp
        )
    }
}