package com.example.data.local.entity

import androidx.room.Entity
import com.example.domain.model.Recipe

@Entity(tableName = "want_to_cook", primaryKeys = ["userId", "recipeId"])
data class WantToCookEntity(
    val userId: String,
    val recipeId: String,
    val name: String,
    val imageUrl: String,
    val category: String,
    val addedAt: Long = System.currentTimeMillis()
)

fun WantToCookEntity.toDomain(): Recipe {
    return Recipe(
        id = recipeId,
        name = name,
        cuisine = "",
        imageUrl = imageUrl,
        category = category,
        ingredients = emptyList(),
        instructions = emptyList()
    )
}

fun Recipe.toWantToCookEntity(userId: String): WantToCookEntity {
    return WantToCookEntity(
        userId = userId,
        recipeId = id,
        name = name,
        imageUrl = imageUrl,
        category = category,
        addedAt = System.currentTimeMillis()
    )
}