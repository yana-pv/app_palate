package com.example.domain.usecase

import com.example.domain.model.Recipe
import com.example.domain.repository.UserRecipeRepository
import javax.inject.Inject

class AddToWantToCookUseCase @Inject constructor(
    private val repository: UserRecipeRepository
) {
    suspend operator fun invoke(userId: String, recipe: Recipe) = repository.addToWantToCook(userId, recipe)
}