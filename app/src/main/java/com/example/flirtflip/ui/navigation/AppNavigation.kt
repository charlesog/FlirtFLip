package com.example.flirtflip.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flirtflip.ui.screens.*

/**
 * Definición de las rutas de la aplicación.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object CompleteProfile : Screen("complete_profile")
    object Home : Screen("home")
    object ProfileEdit : Screen("profile_edit")
    object ChatList : Screen("chat_list")
    object ChatDetail : Screen("chat_detail/{chatId}")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        // --- PANTALLA DE LOGIN ---
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        // Limpiamos el stack para que no se pueda volver al login con el botón atrás
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToProfile = {
                    navController.navigate(Screen.CompleteProfile.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigateUp()
                }
            )
        }

        composable(Screen.CompleteProfile.route) {
            // TODO: Implementar CompleteProfileScreen
        }

        // --- PANTALLA PRINCIPAL (SWIPE) ---
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToMatches = { /* TODO: Implementar pantalla de matches */ },
                onNavigateToChatList = {
                    navController.navigate(Screen.ChatList.route)
                },
                onNavigateToChatDetail = { chatId ->
                    navController.navigate("chat_detail/$chatId")
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.ProfileEdit.route)
                }
            )
        }

        composable(Screen.ChatList.route) {
            ChatListScreen(
                onChatClick = { chatId ->
                    navController.navigate("chat_detail/$chatId")
                }
            )
        }

        composable(
            route = Screen.ChatDetail.route,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType }
            )
        ) {
            ChatDetailScreen(
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(Screen.ProfileEdit.route) {
            ProfileEditScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}