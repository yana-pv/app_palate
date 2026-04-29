package com.example.data.repository

import com.example.domain.model.ShoppingItem
import com.example.domain.repository.ShoppingListRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingListRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ShoppingListRepository {

    private val shoppingCollection = firestore.collection("shopping_lists")

    override fun getShoppingList(userId: String): Flow<List<ShoppingItem>> = callbackFlow {
        val subscription = shoppingCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ShoppingItem::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun addShoppingItem(item: ShoppingItem) {
        shoppingCollection.add(item).await()
    }

    override suspend fun addOrUpdateShoppingItem(item: ShoppingItem) {
        val existingItem = shoppingCollection
            .whereEqualTo("userId", item.userId)
            .whereEqualTo("name", item.name)
            .whereEqualTo("unit", item.unit)
            .whereEqualTo("isChecked", false)
            .get()
            .await()
            .documents
            .firstOrNull()

        if (existingItem != null) {
            val currentAmountStr = existingItem.getString("amount") ?: ""
            val addedAmountStr = item.amount
            
            val currentAmount = currentAmountStr.toDoubleOrNull()
            val addedAmount = addedAmountStr.toDoubleOrNull()

            if (currentAmount != null && addedAmount != null) {
                val newAmount = currentAmount + addedAmount
                val formattedAmount = if (newAmount % 1.0 == 0.0) newAmount.toInt().toString() else newAmount.toString()
                shoppingCollection.document(existingItem.id).update("amount", formattedAmount).await()
            } else if (currentAmount == null && addedAmount == null && currentAmountStr == addedAmountStr) {
                // do nothing to avoid duplicates
            } else {
                shoppingCollection.add(item).await()
            }
        } else {
            shoppingCollection.add(item).await()
        }
    }

    override suspend fun updateShoppingItem(item: ShoppingItem) {
        if (item.id.isNotEmpty()) {
            val updates = mapOf(
                "name" to item.name,
                "amount" to item.amount,
                "unit" to item.unit,
                "isChecked" to item.isChecked,
                "userId" to item.userId
            )
            shoppingCollection.document(item.id).update(updates).await()
        }
    }

    override suspend fun deleteShoppingItems(itemIds: List<String>) {
        val batch = firestore.batch()
        itemIds.forEach { id ->
            batch.delete(shoppingCollection.document(id))
        }
        batch.commit().await()
    }

    override suspend fun addIngredientsToShoppingList(
        userId: String,
        recipeId: String,
        ingredients: List<ShoppingItem>
    ) {
        ingredients.forEach { item ->
            addOrUpdateShoppingItem(item.copy(userId = userId))
        }
        
        val recordRef = firestore.collection("user_recipe_additions")
            .document("${userId}_${recipeId}")
        firestore.batch().set(recordRef, mapOf("added" to true, "timestamp" to System.currentTimeMillis())).commit().await()
    }

    override suspend fun isRecipeIngredientsAlreadyAdded(
        userId: String, 
        recipeId: String, 
        ingredientNames: List<String>
    ): Boolean {
        val doc = firestore.collection("user_recipe_additions")
            .document("${userId}_${recipeId}")
            .get()
            .await()
        
        if (!doc.exists()) return false

        val currentList = shoppingCollection
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(ShoppingItem::class.java) }

        val activeItems = currentList.filter { !it.isChecked }.map { it.name.lowercase() }
        
        return ingredientNames.any { name -> activeItems.contains(name.lowercase()) }
    }
}
