package com.example.palate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.auth.ui.LoginScreen
import com.example.auth.ui.RegisterScreen
import com.example.auth.ui.StartupScreen
import com.example.main.MainScreen
import com.example.navigation.Destination
import com.example.recipe_detail.RecipeDetailScreen

@Composable
fun PalateNavGraph(
    navController: NavHostController,
    navigator: PalateNavigator,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Startup.route,
        modifier = modifier
    ) {
        composable(Destination.Startup.route) {
            StartupScreen(navController)
        }
        composable(Destination.Login.route) {
            LoginScreen(navController)
        }
        composable(Destination.Register.route) {
            RegisterScreen(navController)
        }

        composable(Destination.Home.route) {
            MainScreen(
                onRecipeClick = { recipeId ->
                    navigator.openRecipeDetail(recipeId)
                },
                onLogout = {
                    navController.navigate(Destination.Startup.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Destination.RecipeDetail.route) {
            RecipeDetailScreen(
                onBackClick = { navigator.navigateUp() }
            )
        }
    }
}
