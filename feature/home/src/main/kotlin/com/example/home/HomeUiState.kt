package com.example.home

import com.example.domain.model.Category
import com.example.domain.model.RecipePreview

data class HomeUiState(
    val isLoading: Boolean = false,
    val recipes: List<RecipePreview> = emptyList(),
    val categories: List<Category> = emptyList(),
    val cuisines: List<String> = emptyList(),
    val selectedCategoryIds: Set<String> = setOf("all"),
    val selectedCuisines: Set<String> = emptySet(),
    val searchQuery: String = "",
    val isFilterSheetVisible: Boolean = false,
    val errorMessage: String? = null
)
