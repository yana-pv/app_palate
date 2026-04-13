package com.example.network.api

import com.example.network.dto.CategoryResponse
import com.example.network.dto.MealResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TheMealDbApi {

    @GET("categories.php")
    suspend fun getCategories(): Response<CategoryResponse>

    @GET("filter.php")
    suspend fun getRecipesByCategory(
        @Query("c") category: String
    ): Response<MealResponse>

    @GET("search.php")
    suspend fun searchRecipes(
        @Query("s") query: String
    ): Response<MealResponse>

    @GET("list.php?a=list")
    suspend fun getCuisines(): Response<MealResponse>

    @GET("filter.php")
    suspend fun getRecipesByCuisine(
        @Query("a") cuisine: String
    ): Response<MealResponse>
}