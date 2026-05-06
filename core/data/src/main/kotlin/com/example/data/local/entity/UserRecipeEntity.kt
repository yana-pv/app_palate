package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Ingredient
import com.example.domain.model.UserRecipe

@Entity(tableName = "user_recipes")
data class UserRecipeEntity(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val userId: String,
    val name: String,
    val imagePath: String? = null,
    val category: String,
    val cookingTime: Int,
    val ingredients: List<Ingredient>,
    val instructions: String,
    val createdAt: Long = System.currentTimeMillis()
)

fun UserRecipeEntity.toDomain() = UserRecipe(
    id = id,
    name = name,
    imagePath = imagePath,
    category = category,
    cookingTime = cookingTime,
    ingredients = ingredients,
    instructions = instructions,
    createdAt = createdAt
)

fun UserRecipe.toEntity(userId: String) = UserRecipeEntity(
    id = id,
    userId = userId,
    name = name,
    imagePath = imagePath,
    category = category,
    cookingTime = cookingTime,
    ingredients = ingredients,
    instructions = instructions,
    createdAt = createdAt
)
