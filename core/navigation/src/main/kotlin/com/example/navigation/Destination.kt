package com.example.navigation

sealed class Destination(val route: String) {
    data object Startup : Destination("startup")
    data object Login : Destination("login")
    data object Register : Destination("register")
    data object Home : Destination("home")
    data object Plan : Destination("plan")
    data object MyRecipes : Destination("my_recipes")
    data object ShoppingList : Destination("shopping_list")
    data object Profile : Destination("profile")

    data object RecipeDetail : Destination("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_detail/$recipeId"
    }

    companion object {
        val bottomNavItems = listOf(
            Plan,
            MyRecipes,
            Home,
            ShoppingList,
            Profile
        )
    }
}

interface Navigator {
    fun navigateTo(destination: Destination)
    fun navigateUp()
}

interface HomeNavigator : Navigator {
    fun openRecipeDetail(recipeId: String)
}
