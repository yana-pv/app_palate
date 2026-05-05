package com.example.domain.model

data class CookedRecipe(
    val recipeId: String,
    val name: String,
    val imageUrl: String,
    val category: String,
    val userRating: Int,
    val userNote: String,
    val userPhotoPath: String?,
    val cookedAt: Long
)