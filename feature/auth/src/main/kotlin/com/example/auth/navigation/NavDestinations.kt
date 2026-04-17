package com.example.auth.navigation

sealed class Screen(val route: String) {
    object Startup : Screen("startup")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
}