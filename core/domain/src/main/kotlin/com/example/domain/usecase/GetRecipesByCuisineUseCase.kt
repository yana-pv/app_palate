package com.example.domain.usecase

import com.example.domain.model.RecipePreview
import com.example.domain.repository.RecipeRepository
import javax.inject.Inject

class GetRecipesByCuisineUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(cuisine: String, language: String = "en"): List<RecipePreview> {
        return repository.getRecipesByCuisine(cuisine, language)
    }
}
