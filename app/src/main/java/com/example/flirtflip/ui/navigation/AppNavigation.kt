package com.example.flirtflip.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flirtflip.ui.screens.ChatDetailScreen
import com.example.flirtflip.ui.screens.ChatListScreen
import com.example.flirtflip.ui.screens.HomeScreen
import com.example.flirtflip.ui.screens.LegalScreen
import com.example.flirtflip.ui.screens.LoginScreen
import com.example.flirtflip.ui.screens.ProfileEditScreen
import com.example.flirtflip.ui.screens.SignUpScreen
import com.example.flirtflip.ui.screens.WelcomeBotScreen
import com.example.flirtflip.ui.theme.ThemePreference

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object WelcomeBot : Screen("welcome_bot")
    object CompleteProfile : Screen("complete_profile")
    object Home : Screen("home")
    object ProfileEdit : Screen("profile_edit")
    object ChatList : Screen("chat_list")
    object ChatDetail : Screen("chat_detail/{chatId}")
    object Legal : Screen("legal")
}

@Composable
fun AppNavigation(
    themePreference: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        // ── LOGIN ─────────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = { showWelcome ->
                    // showWelcome = true  → primera vez, mostrar bot
                    // showWelcome = false → ya lo vio antes, ir directo a Home
                    val destination =
                        if (showWelcome) Screen.WelcomeBot.route else Screen.Home.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        // ── REGISTRO ──────────────────────────────────────────────────────
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToProfile = {
                    // Cuenta nueva → completar perfil
                    navController.navigate(Screen.CompleteProfile.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigateUp()
                }
            )
        }

        // ── COMPLETAR PERFIL (cuenta nueva) ───────────────────────────────
        composable(Screen.CompleteProfile.route) {
            ProfileEditScreen(
                onNavigateBack = {
                    // Siempre va al bot: es la primera vez seguro
                    navController.navigate(Screen.WelcomeBot.route) {
                        popUpTo(Screen.CompleteProfile.route) { inclusive = true }
                    }
                }
            )
        }

        // ── BOT DE BIENVENIDA ─────────────────────────────────────────────
        composable(Screen.WelcomeBot.route) {
            WelcomeBotScreen(
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.WelcomeBot.route) { inclusive = true }
                    }
                }
            )
        }

        // ── HOME ──────────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            val currentEntry = it
            HomeScreen(
                onNavigateToChatList = {
                    navController.navigate(Screen.ChatList.route)
                },
                onNavigateToChatDetail = { chatId ->
                    navController.navigate("chat_detail/$chatId")
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.ProfileEdit.route)
                },
                onNavigateToLegal = {
                    navController.navigate(Screen.Legal.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                savedStateHandle = currentEntry.savedStateHandle,
                themePreference = themePreference,
                onThemeChange = onThemeChange
            )
        }

        // ── CHAT LIST ─────────────────────────────────────────────────────
        composable(Screen.ChatList.route) {
            ChatListScreen(
                onChatClick = { chatId ->
                    navController.navigate("chat_detail/$chatId")
                },
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        // ── CHAT DETAIL ───────────────────────────────────────────────────
        composable(
            route = Screen.ChatDetail.route,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) {
            ChatDetailScreen(
                onBack = { navController.navigateUp() }
            )
        }

        // ── EDITAR PERFIL (desde Home) ────────────────────────────────────
        composable(Screen.ProfileEdit.route) {
            ProfileEditScreen(
                onNavigateBack = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("goToProfileTab", true)
                    navController.navigateUp()
                }
            )
        }

        // ── LEGAL ─────────────────────────────────────────────────────────
        composable(Screen.Legal.route) {
            LegalScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}