package com.example.navigation

sealed class Destination(val route: String) {
    data object Startup : Destination("startup")
    data object Login : Destination("login")
    data object Register : Destination("register")
    data object Home : Destination("home?date={date}&mealType={mealType}") {
        fun createRoute(date: String? = null, mealType: String? = null): String {
            return if (date != null && mealType != null) "home?date=$date&mealType=$mealType" else "home"
        }
    }
    data object Plan : Destination("plan")
    data object MyRecipes : Destination("my_recipes?date={date}&mealType={mealType}") {
        fun createRoute(date: String? = null, mealType: String? = null): String {
            return if (date != null && mealType != null) "my_recipes?date=$date&mealType=$mealType" else "my_recipes"
        }
    }
    data object ShoppingList : Destination("shopping_list")
    data object Profile : Destination("profile")

    data object CreateRecipe : Destination("create_recipe")


    data object RecipeDetail : Destination("recipe_detail/{recipeId}?date={date}&mealType={mealType}") {
        fun createRoute(recipeId: String, date: String? = null, mealType: String? = null): String {
            return buildString {
                append("recipe_detail/$recipeId")
                if (date != null && mealType != null) {
                    append("?date=$date&mealType=$mealType")
                }
            }
        }
    }

    data object CookedNote : Destination("cooked_note/{recipeId}") {
        fun createRoute(recipeId: String) = "cooked_note/$recipeId"
    }

    data object MyRecipeDetail : Destination("my_recipe_detail/{recipeId}?date={date}&mealType={mealType}") {
        fun createRoute(recipeId: String, date: String? = null, mealType: String? = null): String {
            return buildString {
                append("my_recipe_detail/$recipeId")
                if (date != null && mealType != null) {
                    append("?date=$date&mealType=$mealType")
                }
            }
        }
    }

    data object EditRecipe : Destination("edit_recipe/{recipeId}") {
        fun createRoute(recipeId: String) = "edit_recipe/$recipeId"
    }
}

interface Navigator {
    fun navigateTo(destination: Destination)
    fun navigateUp()
}

interface HomeNavigator : Navigator {
    fun openRecipeDetail(recipeId: String, date: String? = null, mealType: String? = null)
}
