package com.example.data.repository



import com.example.data.mapper.toDomain
import com.example.domain.repository.RecipeRepository
import com.example.network.api.TheMealDbApi
import com.example.domain.model.Category
import com.example.domain.model.RecipePreview
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val api: TheMealDbApi
) : RecipeRepository {

    override suspend fun getCategories(): List<Category> {
        val response = api.getCategories()
        return if (response.isSuccessful) {
            response.body()?.categories?.map { it.toDomain() } ?: emptyList()
        } else {
            emptyList()
        }
    }

    override suspend fun getRecipesByCategory(categoryName: String): List<RecipePreview> {
        val response = api.getRecipesByCategory(categoryName)
        return if (response.isSuccessful) {
            response.body()?.meals?.map { it.toDomain() } ?: emptyList()
        } else {
            emptyList()
        }
    }

    override suspend fun getCuisines(): List<String> {
        val response = api.getCuisines()
        return if (response.isSuccessful) {
            response.body()?.meals?.mapNotNull { it.area } ?: emptyList()
        } else {
            emptyList()
        }
    }

    override suspend fun getRecipesByCuisine(cuisine: String): List<RecipePreview> {
        val response = api.getRecipesByCuisine(cuisine)
        return if (response.isSuccessful) {
            response.body()?.meals?.map { it.toDomain() } ?: emptyList()
        } else {
            emptyList()
        }
    }
}