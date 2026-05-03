package com.example.domain.usecase

import com.example.domain.model.Category
import com.example.domain.model.RecipePreview
import com.example.domain.model.Cuisine
import com.example.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetHomeDataUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(language: String = "en"): Flow<HomeData> = channelFlow {
        val categories = try {
            repository.getCategories(language)
        } catch (e: Exception) {
            emptyList()
        }
        
        val cuisines = try {
            repository.getCuisines(language)
        } catch (e: Exception) {
            emptyList()
        }

        val recipesByCategory = mutableMapOf<Category, List<RecipePreview>>()

        send(HomeData(categories, cuisines, emptyMap()))

        if (categories.isEmpty()) {
            return@channelFlow
        }

        categories.take(8).forEach { category ->
            launch {
                try {
                    val recipes = repository.getRecipesByCategory(category.originalName, category.id, language)
                    
                    val currentData = synchronized(recipesByCategory) {
                        recipesByCategory[category] = recipes
                        recipesByCategory.toMap()
                    }
                    
                    send(HomeData(categories, cuisines, currentData))
                } catch (e: Exception) {
                    // Ignore individual category errors
                }
            }
        }
    }
}

data class HomeData(
    val categories: List<Category>,
    val cuisines: List<Cuisine>,
    val recipesByCategory: Map<Category, List<RecipePreview>>
)
