package com.example.data.repository

import com.example.data.local.dao.RecipeDao
import com.example.data.local.entity.toDomain
import com.example.data.local.entity.toEntity
import com.example.data.mapper.toDomain
import com.example.data.mapper.toDomainPreview
import com.example.domain.repository.RecipeRepository
import com.example.network.api.TheMealDbApi
import com.example.domain.model.Category
import com.example.domain.model.Recipe
import com.example.domain.model.RecipePreview
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val api: TheMealDbApi,
    private val dao: RecipeDao
) : RecipeRepository {

    override suspend fun getCategories(): List<Category> {
        return try {
            val response = api.getCategories()
            if (response.isSuccessful) {
                val categories = response.body()?.categories?.map { it.toDomain() } ?: emptyList()
                dao.clearCategories()
                dao.insertCategories(categories.map { it.toEntity() })
                categories
            } else {
                dao.getCategories().first().map { it.toDomain() }
            }
        } catch (e: Exception) {
            dao.getCategories().first().map { it.toDomain() }
        }
    }

    override suspend fun getRecipesByCategory(categoryName: String): List<RecipePreview> {
        return try {
            val response = api.getRecipesByCategory(categoryName)
            if (response.isSuccessful) {
                val recipes = response.body()?.meals?.map { it.toDomainPreview() } ?: emptyList()
                dao.clearPreviewsByCategory(categoryName)
                dao.insertRecipePreviews(recipes.map { it.toEntity() })
                recipes
            } else {
                dao.getRecipesByCategory(categoryName).first().map { it.toDomain() }
            }
        } catch (e: Exception) {
            dao.getRecipesByCategory(categoryName).first().map { it.toDomain() }
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
        return try {
            val response = api.getRecipesByCuisine(cuisine)
            if (response.isSuccessful) {
                val recipes = response.body()?.meals?.map { it.toDomainPreview() } ?: emptyList()
                dao.clearPreviewsByCuisine(cuisine)
                dao.insertRecipePreviews(recipes.map { it.toEntity(cuisine) })
                recipes
            } else {
                dao.getRecipesByCuisine(cuisine).first().map { it.toDomain() }
            }
        } catch (e: Exception) {
            dao.getRecipesByCuisine(cuisine).first().map { it.toDomain() }
        }
    }

    override suspend fun getRecipeById(id: String): Recipe? {
        return try {
            val response = api.getRecipeById(id)
            if (response.isSuccessful) {
                val recipe = response.body()?.meals?.firstOrNull()?.toDomain()
                recipe?.let { dao.insertRecipe(it.toEntity()) }
                recipe
            } else {
                dao.getRecipeById(id)?.toDomain()
            }
        } catch (e: Exception) {
            dao.getRecipeById(id)?.toDomain()
        }
    }
}
