package com.example.palate.feature.home

import com.example.domain.model.Category
import com.example.domain.model.RecipePreview

sealed interface HomeUiState {
    object Loading : HomeUiState

    data class Success(
        val categories: List<Category>,
        val cuisines: List<String> = emptyList(),
        val recipes: List<RecipePreview>,
        val selectedCategoryId: String? = null,
        val selectedCuisine: String? = null,
        val searchQuery: String = "",
        val isFilterSheetVisible: Boolean = false
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}