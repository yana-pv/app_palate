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
import com.example.domain.model.Cuisine
import com.example.domain.translation.Translator
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val api: TheMealDbApi,
    private val dao: RecipeDao,
    private val translator: Translator
) : RecipeRepository {

    override suspend fun getCategories(language: String): List<Category> {
        return try {
            val response = api.getCategories()
            if (response.isSuccessful) {
                val categories = response.body()?.categories?.map { it.toDomain() } ?: emptyList()
                val names = categories.map { it.name }
                val translated = translator.translateList(names, language)
                val translatedCategories = categories.mapIndexed { index, category ->
                    category.copy(name = translated.getOrElse(index) { category.name })
                }

                if (translatedCategories.isNotEmpty()) {
                    dao.clearCategories()
                    dao.insertCategories(translatedCategories.map { it.toEntity() })
                }
                translatedCategories
            } else {
                dao.getCategories().first().map { it.toDomain() }
            }
        } catch (e: Exception) {
            dao.getCategories().first().map { it.toDomain() }
        }
    }

    override suspend fun getRecipesByCategory(categoryName: String, categoryId: String, language: String): List<RecipePreview> {
        return try {
            val response = api.getRecipesByCategory(categoryName)
            if (response.isSuccessful) {
                val localizedCategoryName = translator.translate(categoryName, language)

                val apiRecipes = response.body()?.meals?.map { 
                    it.toDomainPreview(categoryName = localizedCategoryName, categoryId = categoryId) 
                } ?: emptyList()
                
                val existingRecipes = dao.getPreviewsByIds(apiRecipes.map { it.id })
                val existingCuisines = existingRecipes.associate { it.id to it.cuisine }
                
                var recipes = apiRecipes.map { recipe ->
                    recipe.copy(cuisine = existingCuisines[recipe.id] ?: "")
                }

                recipes = translateRecipePreviews(recipes, language)

                recipes.forEach { recipe ->
                    val existing = dao.getPreviewsByIds(listOf(recipe.id)).firstOrNull()
                    if (existing != null) {
                        dao.updateRecipePreview(recipe.toEntity(cuisine = recipe.cuisine.ifEmpty { existing.cuisine }))
                    } else {
                        dao.insertRecipePreviews(listOf(recipe.toEntity()))
                    }
                }
                recipes
            } else {
                dao.getRecipesByCategory(categoryName).first().map { it.toDomain() }
            }
        } catch (e: Exception) {
            dao.getRecipesByCategory(categoryName).first().map { it.toDomain() }
        }
    }

    override suspend fun getCuisines(language: String): List<Cuisine> {
        return try {
            val response = api.getCuisines()
            val originalCuisines = if (response.isSuccessful) {
                response.body()?.meals?.mapNotNull { it.area } ?: emptyList()
            } else emptyList()
            
            val translated = translator.translateList(originalCuisines, language)
            originalCuisines.mapIndexed { index, original ->
                Cuisine(name = translated.getOrElse(index) { original }, originalName = original)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getRecipesByCuisine(cuisine: String, language: String): List<RecipePreview> {
        return try {
            val response = api.getRecipesByCuisine(cuisine)
            if (response.isSuccessful) {
                var recipes = response.body()?.meals?.map { 
                    it.toDomainPreview(cuisine = cuisine) 
                } ?: emptyList()
                
                val existingRecipes = dao.getPreviewsByIds(recipes.map { it.id })
                val existingCategories = existingRecipes.associate { it.id to (it.categoryName to it.categoryId) }
                
                recipes = recipes.map { recipe ->
                    val (catName, catId) = existingCategories[recipe.id] ?: ("" to "")
                    recipe.copy(
                        categoryName = recipe.categoryName.ifEmpty { catName },
                        categoryId = recipe.categoryId.ifEmpty { catId }
                    )
                }

                recipes = translateRecipePreviews(recipes, language)

                recipes.forEach { recipe ->
                    val existing = dao.getPreviewsByIds(listOf(recipe.id)).firstOrNull()
                    if (existing != null) {
                        val updated = recipe.toEntity(cuisine = cuisine).copy(
                            categoryName = recipe.categoryName.ifEmpty { existing.categoryName },
                            categoryId = recipe.categoryId.ifEmpty { existing.categoryId }
                        )
                        dao.updateRecipePreview(updated)
                    } else {
                        dao.insertRecipePreviews(listOf(recipe.toEntity(cuisine)))
                    }
                }
                recipes
            } else {
                dao.getRecipesByCuisine(cuisine).first().map { it.toDomain() }
            }
        } catch (e: Exception) {
            dao.getRecipesByCuisine(cuisine).first().map { it.toDomain() }
        }
    }

    override suspend fun getRecipeById(id: String, language: String): Recipe? {
        return try {
            val response = api.getRecipeById(id)
            if (response.isSuccessful) {
                var recipe = response.body()?.meals?.firstOrNull()?.toDomain()
                if (recipe != null) {
                    recipe = recipe.copy(
                        name = translator.translate(recipe.name, language),
                        instructions = translator.translateList(recipe.instructions, language),
                        category = translator.translate(recipe.category, language),
                        cuisine = translator.translate(recipe.cuisine, language),
                        ingredients = recipe.ingredients.map { ingredient ->
                            ingredient.copy(
                                name = translator.translate(ingredient.name, language),
                                amount = translator.translate(ingredient.amount, language)
                            )
                        }
                    )
                }
                recipe?.let { dao.insertRecipe(it.toEntity()) }
                recipe
            } else {
                dao.getRecipeById(id)?.toDomain()
            }
        } catch (e: Exception) {
            dao.getRecipeById(id)?.toDomain()
        }
    }

    private suspend fun translateRecipePreviews(recipes: List<RecipePreview>, language: String): List<RecipePreview> {
        if (recipes.isEmpty()) return recipes
        return try {
            val names = recipes.map { it.name }
            val translatedNames = translator.translateList(names, language)
            
            val categories = recipes.map { it.categoryName }.distinct()
            val translatedCategories = translator.translateList(categories, language)
            val categoryMap = categories.zip(translatedCategories).toMap()

            recipes.mapIndexed { index, recipe ->
                recipe.copy(
                    name = translatedNames.getOrElse(index) { recipe.name },
                    categoryName = categoryMap[recipe.categoryName] ?: recipe.categoryName
                )
            }
        } catch (e: Exception) {
            recipes
        }
    }
}
