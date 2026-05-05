package com.example.domain.repository

import com.example.domain.model.CookedRecipe
import com.example.domain.model.Recipe
import com.example.domain.model.UserRecipe
import kotlinx.coroutines.flow.Flow


interface UserRecipeRepository {

    // Want To Cook
    suspend fun addToWantToCook(userId: String, recipe: Recipe)
    suspend fun removeFromWantToCook(userId: String, recipeId: String)
    fun getWantToCookRecipes(userId: String): Flow<List<Recipe>>
    suspend fun isInWantToCook(userId: String, recipeId: String): Boolean

    // Cooked
    suspend fun addToCooked(userId: String, recipe: Recipe, rating: Int, note: String, photoPath: String?)
    suspend fun updateCooked(userId: String, recipeId: String, rating: Int, note: String, photoPath: String?)
    suspend fun removeFromCooked(userId: String, recipeId: String)
    fun getCookedRecipes(userId: String): Flow<List<CookedRecipe>>
    suspend fun getCookedRecipeById(userId: String, recipeId: String): CookedRecipe?

    // User Recipes (свои рецепты)
    suspend fun saveUserRecipe(userId: String, recipe: UserRecipe)
    suspend fun updateUserRecipe(userId: String, recipe: UserRecipe)
    suspend fun deleteUserRecipe(userId: String, recipeId: String)
    fun getUserRecipes(userId: String): Flow<List<UserRecipe>>
    suspend fun getUserRecipeById(userId: String, id: String): UserRecipe?

    suspend fun addUserRecipeToCooked(userId: String, recipe: UserRecipe, rating: Int, note: String, photoPath: String?)

    fun getCookedCount(userId: String): Flow<Int>
    fun getWantToCookCount(userId: String): Flow<Int>
    fun getUserRecipesCount(userId: String): Flow<Int>
}