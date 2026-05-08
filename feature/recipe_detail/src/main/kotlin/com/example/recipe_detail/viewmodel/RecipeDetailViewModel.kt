package com.example.recipe_detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.MealPlanRepository
import com.example.domain.repository.SettingsRepository
import com.example.domain.repository.ShoppingListRepository
import com.example.domain.repository.UserRecipeRepository
import com.example.domain.repository.UserRepository
import com.example.domain.usecase.GetRecipeByIdUseCase
import com.example.recipe_detail.RecipeDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val shoppingRepository: ShoppingListRepository,
    private val mealPlanRepository: MealPlanRepository,
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val userRecipeRepository: UserRecipeRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recipeId: String = checkNotNull(savedStateHandle["recipeId"])
    private val selectionDate: String? = savedStateHandle["date"]
    private val selectionMealType: String? = savedStateHandle["mealType"]

    private val userId: String
        get() = authRepository.getCurrentUser()?.id ?: ""

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

    private val _uiState = MutableStateFlow(RecipeDetailUiState(
        isLoading = true,
        isSelectionMode = isSelectionMode
    ))
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.getLanguage().collect { language ->
                loadRecipe(language)
            }
        }
    }

    fun loadRecipe(language: String = "en") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val recipe = getRecipeByIdUseCase(recipeId, language)
                if (recipe != null) {
                    _uiState.update { it.copy(isLoading = false, recipe = recipe) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Recipe not found") }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Failed to load recipe. Checking offline storage..." 
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    fun addToWantToCook() {
        viewModelScope.launch {
            val recipe = _uiState.value.recipe ?: return@launch
            userRecipeRepository.addToWantToCook(userId, recipe)
        }
    }

    fun addToShoppingList(recipeId: String) {
        viewModelScope.launch {
            val recipe = _uiState.value.recipe ?: return@launch

        }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun addIngredientsToShoppingList(force: Boolean = false) {
        val recipe = _uiState.value.recipe ?: return
        val userId = authRepository.getCurrentUser()?.id ?: return

        viewModelScope.launch {
            if (!force) {
                val alreadyAdded = shoppingRepository.isRecipeIngredientsAlreadyAdded(
                    userId = userId,
                    recipeId = recipe.id,
                    ingredientNames = recipe.ingredients.map { it.name }
                )
                if (alreadyAdded) {
                    _uiState.update { it.copy(showAlreadyAddedDialog = true) }
                    return@launch
                }
            }

            val items = recipe.ingredients.map { ingredient ->
                val (amount, unit) = parseIngredientAmount(ingredient.amount)
                com.example.domain.model.ShoppingItem(
                    name = ingredient.name,
                    amount = amount,
                    unit = unit,
                    userId = userId
                )
            }

            try {
                shoppingRepository.addIngredientsToShoppingList(userId, recipe.id, items)
                _uiState.update { it.copy(
                    showAlreadyAddedDialog = false,
                    successMessage = "Ingredients added to shopping list"
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to add ingredients") }
            }
        }
    }

    private fun parseIngredientAmount(amountStr: String): Pair<String, String> {
        val trimmed = amountStr.trim()
        if (trimmed.isEmpty()) return Pair("", "")

        val regex = Regex("""^([\d\s./,]+)(.*)$""")
        val matchResult = regex.find(trimmed)

        if (matchResult != null) {
            val amountPart = matchResult.groupValues[1].trim()
            val unitPart = matchResult.groupValues[2].trim()

            val numericAmount = if (amountPart.contains("/")) {
                try {
                    val parts = amountPart.split("/")
                    if (parts.size == 2) {
                        parts[0].trim().toDouble() / parts[1].trim().toDouble()
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            } else {
                amountPart.replace(",", ".").toDoubleOrNull()
            }

            return if (numericAmount != null) {
                val formatted = if (numericAmount % 1.0 == 0.0) numericAmount.toInt().toString() else numericAmount.toString()
                Pair(formatted, unitPart)
            } else {
                Pair("", "")
            }
        }
        return Pair("", "")
    }

    fun dismissAlreadyAddedDialog() {
        _uiState.update { it.copy(showAlreadyAddedDialog = false) }
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
                recipeImageUrl = recipe.imageUrl,
                recipeCategory = recipe.category,
                isUserRecipe = false
            )
            mealPlanRepository.addMealPlanItem(item)
            _uiState.update { it.copy(isSelected = true) }
        }
    }
}
