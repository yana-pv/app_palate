package com.example.domain.usecase

import com.example.domain.repository.UserRecipeRepository
import javax.inject.Inject

class RemoveFromCookedUseCase @Inject constructor(
    private val repository: UserRecipeRepository
) {
    suspend operator fun invoke(userId: String, recipeId: String) {
        repository.removeFromCooked(userId, recipeId)
    }
}