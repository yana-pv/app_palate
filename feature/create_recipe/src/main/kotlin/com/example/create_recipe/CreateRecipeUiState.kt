package com.example.create_recipe

import com.example.domain.model.Ingredient

data class CreateRecipeUiState(
    val name: String = "",
    val cuisine: String = "",
    val category: String = "",
    val ingredients: List<Ingredient> = listOf(Ingredient("", "", "")),
    val instructions: String = "",
    val imageUri: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)