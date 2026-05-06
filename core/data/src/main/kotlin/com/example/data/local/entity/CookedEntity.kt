package com.example.data.local.entity

import androidx.room.Entity
import com.example.domain.model.CookedRecipe

@Entity(tableName = "cooked", primaryKeys = ["userId", "recipeId"])
data class CookedEntity(
    val userId: String,
    val recipeId: String,
    val name: String,
    val imageUrl: String,
    val category: String,
    val userRating: Int = 0,
    val userNote: String = "",
    val userPhotoPath: String? = null,
    val cookedAt: Long = System.currentTimeMillis()
)

fun CookedEntity.toDomain(): CookedRecipe {
    return CookedRecipe(
        recipeId = recipeId,
        name = name,
        imageUrl = imageUrl,
        category = category,
        userRating = userRating,
        userNote = userNote,
        userPhotoPath = userPhotoPath,
        cookedAt = cookedAt
    )
}

fun CookedRecipe.toEntity(userId: String): CookedEntity {
    return CookedEntity(
        userId = userId,
        recipeId = recipeId,
        name = name,
        imageUrl = imageUrl,
        category = category,
        userRating = userRating,
        userNote = userNote,
        userPhotoPath = userPhotoPath,
        cookedAt = cookedAt
    )
}