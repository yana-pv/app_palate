package com.example.data.repository

import com.example.domain.model.MealPlanItem
import com.example.domain.model.MealType
import com.example.domain.repository.MealPlanRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealPlanRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MealPlanRepository {

    private fun getUserMealPlanCollection(userId: String) = 
        firestore.collection("users").document(userId).collection("mealPlans")

    override fun getMealPlanForWeek(userId: String, weekStartDate: LocalDate): Flow<List<MealPlanItem>> = callbackFlow {
        val weekEndDate = weekStartDate.plusDays(6)
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        
        val listener = getUserMealPlanCollection(userId)
            .whereGreaterThanOrEqualTo("date", weekStartDate.format(formatter))
            .whereLessThanOrEqualTo("date", weekEndDate.format(formatter))
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                
                val allItems = mutableListOf<MealPlanItem>()
                
                snapshot?.documents?.forEach { doc ->
                    val dateStr = doc.getString("date") ?: doc.id
                    val date = try {
                        LocalDate.parse(dateStr, formatter)
                    } catch (e: Exception) {
                        return@forEach
                    }
                    
                    MealType.values().forEach { type ->
                        val prefix = type.name.lowercase()
                        val recipeId = doc.getString("${prefix}RecipeId")
                        if (!recipeId.isNullOrEmpty()) {
                            allItems.add(
                                MealPlanItem(
                                    id = "${userId}:::${dateStr}:::${type.name}",
                                    userId = userId,
                                    date = date,
                                    mealType = type,
                                    recipeId = recipeId,
                                    recipeName = doc.getString("${prefix}RecipeName") ?: "",
                                    recipeImageUrl = doc.getString("${prefix}RecipeImageUrl"),
                                    recipeCategory = doc.getString("${prefix}RecipeCategory") ?: "",
                                    isUserRecipe = doc.getBoolean("${prefix}IsUserRecipe") ?: false
                                )
                            )
                        }
                    }
                }
                
                trySend(allItems)
            }
        
        awaitClose { listener.remove() }
    }

    override suspend fun addMealPlanItem(item: MealPlanItem) {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val dateStr = item.date.format(formatter)
        val prefix = item.mealType.name.lowercase()
        
        val data = hashMapOf(
            "date" to dateStr,
            "synced" to true,
            "${prefix}RecipeId" to item.recipeId,
            "${prefix}RecipeName" to item.recipeName,
            "${prefix}RecipeImageUrl" to item.recipeImageUrl,
            "${prefix}RecipeCategory" to item.recipeCategory,
            "${prefix}IsUserRecipe" to item.isUserRecipe
        )
        
        getUserMealPlanCollection(item.userId)
            .document(dateStr)
            .set(data, SetOptions.merge())
            .await()
    }

    override suspend fun removeMealPlanItem(itemId: String) {
        val parts = itemId.split(":::")
        if (parts.size < 3) return
        
        val userId = parts[0]
        val dateStr = parts[1]
        val mealTypeStr = parts[2]
        val prefix = mealTypeStr.lowercase()
        
        val updates = hashMapOf<String, Any?>(
            "${prefix}RecipeId" to FieldValue.delete(),
            "${prefix}RecipeName" to FieldValue.delete(),
            "${prefix}RecipeImageUrl" to FieldValue.delete(),
            "${prefix}RecipeCategory" to FieldValue.delete(),
            "${prefix}IsUserRecipe" to FieldValue.delete()
        )
        
        getUserMealPlanCollection(userId)
            .document(dateStr)
            .update(updates)
            .await()
    }
}
