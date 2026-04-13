package com.example.palate.core.navigation

sealed class Destination(val route: String) {
    data object Home : Destination("home")
    data object RecipeDetail : Destination("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_detail/$recipeId"
    }
    data object Plan : Destination("plan")
    data object MyRecipes : Destination("my_recipes")
    data object ShoppingList : Destination("shopping_list")
    data object Profile : Destination("profile")
}

interface Navigator {
    fun navigateTo(destination: Destination)
    fun navigateUp()
}

interface HomeNavigator : Navigator {
    fun openRecipeDetail(recipeId: String)
}
