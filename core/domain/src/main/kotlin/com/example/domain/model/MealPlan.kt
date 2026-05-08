package com.example.domain.model

import java.time.LocalDate

enum class MealType {
    BREAKFAST, LUNCH, DINNER, SNACK
}

data class MealPlanItem(
    val id: String = "",
    val userId: String = "",
    val date: LocalDate = LocalDate.now(),
    val mealType: MealType = MealType.BREAKFAST,
    val recipeId: String = "",
    val recipeName: String = "",
    val recipeImageUrl: String? = null,
    val recipeCategory: String = "",
    val isUserRecipe: Boolean = false
)
