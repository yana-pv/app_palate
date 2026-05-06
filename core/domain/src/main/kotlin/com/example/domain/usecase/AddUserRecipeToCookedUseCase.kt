package com.example.domain.usecase

import com.example.domain.model.UserRecipe
import com.example.domain.repository.UserRecipeRepository
import javax.inject.Inject

class AddUserRecipeToCookedUseCase @Inject constructor(
private val repository: UserRecipeRepository
) {
    suspend operator fun invoke(userId: String, recipe: UserRecipe, rating: Int, note: String, photoPath: String?) {
        repository.addUserRecipeToCooked(userId, recipe, rating, note, photoPath)
    }
}