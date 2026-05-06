package com.example.domain.usecase

import com.example.domain.repository.UserRecipeRepository
import javax.inject.Inject

class IsInWantToCookUseCase @Inject constructor(
    private val repository: UserRecipeRepository
) {
    suspend operator fun invoke(userId: String, recipeId: String): Boolean {
        return repository.isInWantToCook(userId, recipeId)
    }
}