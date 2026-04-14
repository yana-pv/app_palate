package com.example.domain.model

data class RecipePreview(
    val id: String,
    val name: String,
    val imageUrl: String,
    val categoryName: String,
    val isSaved: Boolean = false
)