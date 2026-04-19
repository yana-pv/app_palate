package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Ingredient
import com.example.domain.model.Recipe

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val cuisine: String,
    val imageUrl: String,
    val category: String,
    val ingredients: List<Ingredient>,
    val instructions: List<String>
)

fun RecipeEntity.toDomain() = Recipe(
    id = id,
    name = name,
    cuisine = cuisine,
    imageUrl = imageUrl,
    category = category,
    ingredients = ingredients,
    instructions = instructions
)

fun Recipe.toEntity() = RecipeEntity(
    id = id,
    name = name,
    cuisine = cuisine,
    imageUrl = imageUrl,
    category = category,
    ingredients = ingredients,
    instructions = instructions
)
