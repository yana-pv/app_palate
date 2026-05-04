package com.example.data.mapper

import com.example.domain.model.Category
import com.example.domain.model.Ingredient
import com.example.domain.model.Recipe
import com.example.domain.model.RecipePreview
import com.example.network.dto.CategoryDto
import com.example.network.dto.MealDto

fun CategoryDto.toDomain(): Category {
    return Category(
        id = this.id,
        name = this.name,
        imageUrl = this.imageUrl,
        originalName = this.name
    )
}

fun MealDto.toDomainPreview(
    categoryName: String? = null,
    categoryId: String? = null,
    cuisine: String? = null
): RecipePreview {
    return RecipePreview(
        id = this.idMeal,
        name = this.name,
        originalName = this.name,
        imageUrl = this.imageUrl,
        categoryName = categoryName ?: this.category ?: "",
        categoryId = categoryId ?: "",
        cuisine = cuisine ?: this.area ?: ""
    )
}

fun MealDto.toDomain(): Recipe {
    val ingredients = mutableListOf<Ingredient>()
    
    val ingredientNames = listOfNotNull(
        strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
        strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
        strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
        strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
    )
    val measures = listOfNotNull(
        strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5,
        strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10,
        strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15,
        strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20
    )

    for (i in ingredientNames.indices) {
        val name = ingredientNames[i]
        val measure = measures.getOrNull(i)
        if (!name.isNullOrBlank()) {
            ingredients.add(Ingredient(name, measure ?: ""))
        }
    }

    val instructions = this.instructions?.split("\r\n", "\n")
        ?.map { it.trim() }
        ?.filter { it.isNotBlank() }
        ?: emptyList()

    return Recipe(
        id = this.idMeal,
        name = this.name,
        cuisine = this.area ?: "",
        imageUrl = this.imageUrl,
        category = this.category ?: "",
        ingredients = ingredients,
        instructions = instructions
    )
}
