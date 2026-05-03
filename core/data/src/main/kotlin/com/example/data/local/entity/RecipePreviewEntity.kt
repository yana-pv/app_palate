package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.RecipePreview

@Entity(tableName = "recipe_previews")
data class RecipePreviewEntity(
    @PrimaryKey val id: String,
    val name: String,
    val originalName: String,
    val imageUrl: String,
    val categoryName: String,
    val categoryId: String = "",
    val cuisine: String? = null
)

fun RecipePreviewEntity.toDomain() = RecipePreview(
    id = id,
    name = name,
    originalName = originalName,
    imageUrl = imageUrl,
    categoryName = categoryName,
    categoryId = categoryId,
    cuisine = cuisine ?: ""
)

fun RecipePreview.toEntity(cuisine: String? = null) = RecipePreviewEntity(
    id = id,
    name = name,
    originalName = originalName,
    imageUrl = imageUrl,
    categoryName = categoryName,
    categoryId = categoryId,
    cuisine = cuisine ?: this.cuisine
)
