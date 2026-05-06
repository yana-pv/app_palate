package com.example.domain.usecase

import com.example.domain.repository.UserRecipeRepository
import javax.inject.Inject
class UpdateCookedUseCase @Inject constructor(
    private val repository: UserRecipeRepository
) {
    suspend operator fun invoke(
        userId: String,
        recipeId: String,
        rating: Int,
        note: String,
        photoPath: String?
    ) {
        repository.updateCooked(userId, recipeId, rating, note, photoPath)
    }
}