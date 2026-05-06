package com.example.domain.usecase

import com.example.domain.model.CookedRecipe
import com.example.domain.repository.UserRecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCookedRecipesUseCase @Inject constructor(
    private val repository: UserRecipeRepository
) {
    operator fun invoke(userId: String): Flow<List<CookedRecipe>> = repository.getCookedRecipes(userId)
}