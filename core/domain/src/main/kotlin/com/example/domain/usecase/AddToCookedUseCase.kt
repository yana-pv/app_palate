package com.example.domain.usecase

import com.example.domain.model.Recipe
import com.example.domain.repository.UserRecipeRepository
import javax.inject.Inject

class AddToCookedUseCase @Inject constructor(
    private val repository: UserRecipeRepository
) {
    suspend operator fun invoke(
        userId: String,
        recipe: Recipe,
        rating: Int,
        note: String,
        photoPath: String?
    ) {
        repository.addToCooked(userId, recipe, rating, note, photoPath)
    }
}
