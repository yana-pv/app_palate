package com.example.palate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.example.my_recipes.MyRecipesScreen
import com.example.my_recipes.viewModel.MyRecipeDetailViewModel
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
                onRecipeClick = { recipeId ->
                    navigator.openRecipeDetail(recipeId)
                },
                onCookedNoteClick = { recipeId ->
                    navigator.openCookedNote(recipeId)
                },
                onMyRecipesClick = { recipeId ->
                    navController.navigate(Destination.MyRecipeDetail.createRoute(recipeId))
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
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) {
            RecipeDetailScreen(
                onBackClick = { navigator.navigateUp() }
            )
        }


        composable(
            route = Destination.MyRecipeDetail.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val viewModel: MyRecipeDetailViewModel = hiltViewModel()

            MyRecipeDetailScreen(
                onBackClick = { navigator.navigateUp() },
                onWantToCookClick = {
                    viewModel.addToWantToCook()
                },
                onToListClick = {
                    // TODO: добавить в список покупок
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

        composable(Destination.MyRecipes.route) {
            MyRecipesScreen(
                onWantToCookClick = { recipeId ->
                    navigator.openRecipeDetail(recipeId)
                },
                onCookedNotesClick = { recipeId ->
                    navigator.openCookedNote(recipeId)
                },
                onMyRecipesClick = { recipeId ->
                    navController.navigate(Destination.MyRecipeDetail.createRoute(recipeId))
                },
                onMyRecipesEditClick = { recipeId ->
                    navController.navigate(Destination.EditRecipe.createRoute(recipeId))
                },
                onCreateRecipeClick = {
                    navController.navigate(Destination.CreateRecipe.route)
                }
            )
        }

        composable(Destination.RecipeDetail.route, arguments = listOf(navArgument("recipeId") { type = NavType.StringType })) {
            RecipeDetailScreen(
                onBackClick = { navigator.navigateUp() }
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
