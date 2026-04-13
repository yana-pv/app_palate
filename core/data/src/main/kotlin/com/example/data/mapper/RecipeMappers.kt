package com.example.data.mapper

import com.example.domain.model.Category
import com.example.domain.model.RecipePreview
import com.example.network.dto.CategoryDto
import com.example.network.dto.MealDto


fun CategoryDto.toDomain(): Category {
    return Category(
        id = this.id,
        name = this.name,
        imageUrl = this.imageUrl
    )
}

fun MealDto.toDomain(): RecipePreview {
    return RecipePreview(
        id = this.idMeal,
        name = this.name,
        imageUrl = this.imageUrl,
        categoryName = this.category ?: ""
    )
}