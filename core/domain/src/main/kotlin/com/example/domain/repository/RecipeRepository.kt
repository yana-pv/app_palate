package com.example.domain.repository

import com.example.domain.model.Category
import com.example.domain.model.Recipe
import com.example.domain.model.RecipePreview


interface RecipeRepository {
    suspend fun getCategories(): List<Category>
    suspend fun getRecipesByCategory(categoryName: String): List<RecipePreview>
    suspend fun getCuisines(): List<String>
    suspend fun getRecipesByCuisine(cuisine: String): List<RecipePreview>
    suspend fun getRecipeById(id: String): Recipe?
}