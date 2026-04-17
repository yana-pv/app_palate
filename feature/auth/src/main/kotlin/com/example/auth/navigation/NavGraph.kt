package com.example.auth.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.auth.ui.AuthViewModel
import com.example.auth.ui.LoginScreen
import com.example.auth.ui.RegisterScreen
import com.example.auth.ui.StartupScreen


@Composable
fun PalateNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Startup.route,
        modifier = modifier
    ) {
        composable(Screen.Startup.route) {
            StartupScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController, authViewModel)
        }
        composable(Screen.Home.route) {
            Text("Главный экран")
        }
    }
}