package com.example.recipe_detail

import com.example.domain.model.Recipe

data class RecipeDetailUiState(
    val isLoading: Boolean = false,
    val recipe: Recipe? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showAlreadyAddedDialog: Boolean = false,
    val isSelectionMode: Boolean = false,
    val isSelected: Boolean = false,
    val isInWantToCook: Boolean = false
)
