package com.example.palate.navigation

import androidx.navigation.NavController
import com.example.navigation.Destination
import com.example.navigation.HomeNavigator
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
class PalateNavigator(private val navController: NavController) : HomeNavigator {

    override fun navigateTo(destination: Destination) {
        navController.navigate(destination.route)
    }

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun openRecipeDetail(recipeId: String) {
        val encodedId = URLEncoder.encode(recipeId, StandardCharsets.UTF_8.toString())
        val route = Destination.RecipeDetail.createRoute(encodedId)
        navController.navigate(route)
    }

    fun openCookedNote(recipeId: String) {
        if (recipeId.isEmpty()) {
            return
        }
        val route = Destination.CookedNote.createRoute(recipeId)
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
        }
    }
}