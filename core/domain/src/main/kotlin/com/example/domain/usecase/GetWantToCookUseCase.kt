package com.example.domain.usecase

import com.example.domain.model.Recipe
import com.example.domain.repository.UserRecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetWantToCookUseCase @Inject constructor(
    private val repository: UserRecipeRepository
) {
    operator fun invoke(userId: String): Flow<List<Recipe>> = repository.getWantToCookRecipes(userId)
}