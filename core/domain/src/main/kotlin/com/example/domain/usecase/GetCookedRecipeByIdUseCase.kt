package com.example.domain.usecase

import com.example.domain.model.CookedRecipe
import com.example.domain.repository.UserRecipeRepository
import javax.inject.Inject
class GetCookedRecipeByIdUseCase @Inject constructor(
    private val repository: UserRecipeRepository
) {
    suspend operator fun invoke(userId: String, recipeId: String): CookedRecipe? {
        return repository.getCookedRecipeById(userId, recipeId)
    }
}
