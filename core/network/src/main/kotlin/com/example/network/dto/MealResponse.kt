package com.example.network.dto

import com.google.gson.annotations.SerializedName

data class MealResponse(
    @SerializedName("meals") val meals: List<MealDto>?
)

data class MealDto(
    @SerializedName("idMeal") val idMeal: String,
    @SerializedName("strMeal") val name: String,
    @SerializedName("strMealThumb") val imageUrl: String,
    @SerializedName("strCategory") val category: String?,
    @SerializedName("strArea") val area: String?
)