package com.example.domain.usecase

import com.example.domain.model.Recipe
import com.example.domain.repository.RecipeRepository
import javax.inject.Inject

class GetRecipeByIdUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(id: String): Recipe? {
        return repository.getRecipeById(id)
    }
}
