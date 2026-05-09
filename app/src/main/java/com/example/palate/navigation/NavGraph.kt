package com.example.palate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.auth.ui.LoginScreen
import com.example.auth.ui.RegisterScreen
import com.example.auth.ui.StartupScreen
import com.example.create_recipe.CreateRecipeScreen
import com.example.main.MainScreen
import com.example.my_recipes.MyRecipeDetailScreen
import com.example.navigation.Destination
import com.example.recipe_detail.CookedNoteScreen
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
                onRecipeClick = { recipeId, date, mealType ->
                    navigator.openRecipeDetail(recipeId, date, mealType)
                },
                onCookedNoteClick = { recipeId ->
                    navigator.openCookedNote(recipeId)
                },
                onMyRecipesClick = { recipeId, date, mealType ->
                    navController.navigate(Destination.MyRecipeDetail.createRoute(recipeId, date, mealType))
                },
                onCreateRecipeClick = {
                    navController.navigate(Destination.CreateRecipe.route)
                },
                onEditRecipeClick = { recipeId ->
                    navController.navigate(Destination.EditRecipe.createRoute(recipeId))
                },
                onLogout = {
                    navController.navigate(Destination.Startup.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Destination.RecipeDetail.route,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("mealType") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
            val mealType = backStackEntry.arguments?.getString("mealType")
            RecipeDetailScreen(
                onBackClick = { navigator.navigateUp() },
                onSelected = {
                    if (date != null && mealType != null) {
                        navController.getBackStackEntry(Destination.Home.route)
                            .savedStateHandle["recipe_selected"] = true
                        navigator.navigateUp()
                    } else {
                        navigator.navigateUp()
                    }
                }
            )
        }

        composable(
            route = Destination.MyRecipeDetail.route,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("mealType") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
            val mealType = backStackEntry.arguments?.getString("mealType")
            MyRecipeDetailScreen(
                onBackClick = { navigator.navigateUp() },
                onSelected = {
                    if (date != null && mealType != null) {
                        navController.getBackStackEntry(Destination.Home.route)
                            .savedStateHandle["recipe_selected"] = true
                        navigator.navigateUp()
                    } else {
                        navigator.navigateUp()
                    }
                }
            )
        }

        composable(
            route = Destination.CookedNote.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) {
            CookedNoteScreen(
                onBackClick = { navigator.navigateUp() },
                onSaved = { navigator.navigateUp() }
            )
        }

        composable(Destination.CreateRecipe.route) {
            CreateRecipeScreen(
                onBackClick = { navigator.navigateUp() },
                onSaved = { navigator.navigateUp() }
            )
        }

        composable(
            route = Destination.EditRecipe.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) {
            CreateRecipeScreen(
                onBackClick = { navigator.navigateUp() },
                onSaved = { navigator.navigateUp() }
            )
        }
    }
}
