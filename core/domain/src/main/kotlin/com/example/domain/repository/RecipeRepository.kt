package com.example.domain.repository

import com.example.domain.model.Category
import com.example.domain.model.Recipe
import com.example.domain.model.RecipePreview
import com.example.domain.model.Cuisine


interface RecipeRepository {
    suspend fun getCategories(language: String = "en"): List<Category>
    suspend fun getRecipesByCategory(categoryName: String, categoryId: String, language: String = "en"): List<RecipePreview>
    suspend fun getCuisines(language: String = "en"): List<Cuisine>
    suspend fun getRecipesByCuisine(cuisine: String, language: String = "en"): List<RecipePreview>
    suspend fun getRecipeById(id: String, language: String = "en"): Recipe?
}