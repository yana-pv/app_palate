package com.example.recipe_detail

import com.example.domain.model.Recipe

sealed interface RecipeDetailUiState {
    data object Loading : RecipeDetailUiState
    data class Success(val recipe: Recipe) : RecipeDetailUiState
    data class Error(val message: String) : RecipeDetailUiState
}
