package com.example.domain.model

data class UserRecipe(
    val id: String,
    val name: String,
    val imagePath: String?,
    val category: String,
    val cookingTime: Int,
    val ingredients: List<Ingredient>,
    val instructions: String,
    val createdAt: Long
)