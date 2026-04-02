package com.example.flirtflip.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flirtflip.ui.screens.HomeScreen
import com.example.flirtflip.ui.screens.LoginScreen
import com.example.flirtflip.ui.screens.ProfileEditScreen
import com.example.flirtflip.ui.screens.SignUpScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object CompleteProfile : Screen("complete_profile")
    object Home : Screen("home")
    object ProfileEdit : Screen("profile_edit")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {

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
            // TODO: CompleteProfileScreen
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToMatches = { /* TODO */ },
                onNavigateToChat = { /* TODO */ },
                onNavigateToProfile = {
                    navController.navigate(Screen.ProfileEdit.route)
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