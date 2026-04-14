package com.example.domain.usecase

import com.example.domain.model.Category
import com.example.domain.model.RecipePreview
import com.example.domain.repository.RecipeRepository
import javax.inject.Inject

class GetHomeDataUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(): HomeData {
        val categories = repository.getCategories()
        val cuisines = repository.getCuisines()
        val recipesByCategory = categories.take(8).associateWith { category ->
            repository.getRecipesByCategory(category.name).map { 
                it.copy(categoryName = category.name) 
            }
        }
        return HomeData(
            categories = categories,
            cuisines = cuisines,
            recipesByCategory = recipesByCategory
        )
    }
}

data class HomeData(
    val categories: List<Category>,
    val cuisines: List<String>,
    val recipesByCategory: Map<Category, List<RecipePreview>>
)