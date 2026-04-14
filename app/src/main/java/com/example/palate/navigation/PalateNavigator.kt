package com.example.palate.navigation

import androidx.navigation.NavController
import com.example.palate.core.navigation.Destination
import com.example.palate.core.navigation.HomeNavigator

class PalateNavigator(private val navController: NavController) : HomeNavigator {

    override fun navigateTo(destination: Destination) {
        navController.navigate(destination.route)
    }

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun openRecipeDetail(recipeId: String) {
        navController.navigate(Destination.RecipeDetail.createRoute(recipeId))
    }
}
