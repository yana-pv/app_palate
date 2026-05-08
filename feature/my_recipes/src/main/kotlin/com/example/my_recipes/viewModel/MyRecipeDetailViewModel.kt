package com.example.my_recipes.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Recipe
import com.example.domain.model.UserRecipe
import com.example.domain.repository.MealPlanRepository
import com.example.domain.repository.UserRecipeRepository
import com.example.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MyRecipeDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userRecipeRepository: UserRecipeRepository,
    private val mealPlanRepository: MealPlanRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String
        get() = userRepository.getCurrentUser()?.id ?: ""

    private val recipeId: String = savedStateHandle["recipeId"] ?: ""
    private val selectionDate: String? = savedStateHandle["date"]
    private val selectionMealType: String? = savedStateHandle["mealType"]

    private val isSelectionMode: Boolean = run {
        val dateStr = selectionDate ?: ""
        val mealStr = selectionMealType ?: ""

        val isDateValid = dateStr.isNotBlank() && 
                          dateStr != "null" && 
                          !dateStr.contains("{") && 
                          dateStr.count { it == '-' } >= 2
                          
        val isMealValid = mealStr.isNotBlank() && 
                          mealStr != "null" && 
                          !mealStr.contains("{")
        
        isDateValid && isMealValid
    }

    private val _uiState = MutableStateFlow(MyRecipeDetailUiState(
        isLoading = true,
        isSelectionMode = isSelectionMode
    ))
    val uiState: StateFlow<MyRecipeDetailUiState> = _uiState.asStateFlow()

    init {
        loadRecipe()
    }

    fun loadRecipe() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val recipe = try {
                userRecipeRepository.getUserRecipeById(userId, recipeId)
            } catch (e: Exception) {
                null
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    recipe = recipe,
                    errorMessage = if (recipe == null) "Recipe not found" else null
                )
            }
        }
    }

    fun addToWantToCook() {
        viewModelScope.launch {
            val recipe = _uiState.value.recipe ?: return@launch

            val apiRecipe = Recipe(
                id = recipe.id,
                name = recipe.name,
                cuisine = recipe.category,
                imageUrl = recipe.imagePath ?: "",
                category = recipe.category,
                ingredients = recipe.ingredients,
                instructions = recipe.instructions.split("\n")
            )
            userRecipeRepository.addToWantToCook(userId, apiRecipe)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun selectRecipe() {
        val dateStr = selectionDate ?: return
        val mealTypeStr = selectionMealType ?: return
        val recipe = uiState.value.recipe ?: return

        viewModelScope.launch {
            val date = LocalDate.parse(dateStr)
            val mealType = com.example.domain.model.MealType.valueOf(mealTypeStr)

            val item = com.example.domain.model.MealPlanItem(
                id = "${userId}:::${date}:::${mealType}",
                userId = userId,
                date = date,
                mealType = mealType,
                recipeId = recipe.id,
                recipeName = recipe.name,
                recipeImageUrl = recipe.imagePath,
                recipeCategory = recipe.category,
                isUserRecipe = true
            )
            mealPlanRepository.addMealPlanItem(item)
            _uiState.update { it.copy(isSelected = true) }
        }
    }
}

data class MyRecipeDetailUiState(
    val isLoading: Boolean = false,
    val recipe: UserRecipe? = null,
    val errorMessage: String? = null,
    val isSelectionMode: Boolean = false,
    val isSelected: Boolean = false
)