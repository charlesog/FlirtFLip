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
import com.example.flirtflip.ui.theme.ThemePreference

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
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

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
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
            ProfileEditScreen(
                onNavigateBack = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.CompleteProfile.route) { inclusive = true }
                    }
                }
            )
        }

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
                        popUpTo(Screen.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                savedStateHandle = currentEntry.savedStateHandle,
                themePreference = themePreference,
                onThemeChange = onThemeChange
            )
        }

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
                    // Señalamos a HomeScreen que vuelva a la pestaña de perfil y recargue
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("goToProfileTab", true)
                    navController.navigateUp()
                }
            )
        }

        composable(Screen.Legal.route) {
            LegalScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}