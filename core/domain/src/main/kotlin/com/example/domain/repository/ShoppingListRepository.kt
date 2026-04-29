package com.example.domain.repository

import com.example.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {
    fun getShoppingList(userId: String): Flow<List<ShoppingItem>>
    suspend fun addShoppingItem(item: ShoppingItem)
    suspend fun addOrUpdateShoppingItem(item: ShoppingItem)
    suspend fun updateShoppingItem(item: ShoppingItem)
    suspend fun deleteShoppingItems(itemIds: List<String>)
    suspend fun addIngredientsToShoppingList(userId: String, recipeId: String, ingredients: List<ShoppingItem>)
    suspend fun isRecipeIngredientsAlreadyAdded(userId: String, recipeId: String, ingredientNames: List<String>): Boolean
}
