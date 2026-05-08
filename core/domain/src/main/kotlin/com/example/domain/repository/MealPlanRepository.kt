package com.example.domain.repository

import com.example.domain.model.MealPlanItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface MealPlanRepository {
    fun getMealPlanForWeek(userId: String, weekStartDate: LocalDate): Flow<List<MealPlanItem>>
    suspend fun addMealPlanItem(item: MealPlanItem)
    suspend fun removeMealPlanItem(itemId: String)
}
