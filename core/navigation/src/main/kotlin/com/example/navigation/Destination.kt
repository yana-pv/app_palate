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

    data object CreateRecipe : Destination("create_recipe")


    data object RecipeDetail : Destination("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_detail/$recipeId"
    }

    data object CookedNote : Destination("cooked_note/{recipeId}") {
        fun createRoute(recipeId: String) = "cooked_note/$recipeId"
    }

    data object MyRecipeDetail : Destination("my_recipe_detail/{recipeId}") {
        fun createRoute(recipeId: String) = "my_recipe_detail/$recipeId"
    }

    data object EditRecipe : Destination("edit_recipe/{recipeId}") {
        fun createRoute(recipeId: String) = "edit_recipe/$recipeId"
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
