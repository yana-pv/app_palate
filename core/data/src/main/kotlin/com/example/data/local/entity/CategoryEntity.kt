package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String,
    val originalName: String
)

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    imageUrl = imageUrl,
    originalName = originalName
)

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    imageUrl = imageUrl,
    originalName = originalName
)
