package com.example.domain.model

data class Recipe(
    val id: String,
    val name: String,
    val cuisine: String,
    val imageUrl: String,
    val category: String,
    val ingredients: List<Ingredient>,
    val instructions: List<String>
)

data class Ingredient(
    val name: String,
    val amount: String
)
