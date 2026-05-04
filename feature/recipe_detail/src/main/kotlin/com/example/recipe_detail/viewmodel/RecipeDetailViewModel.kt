package com.example.recipe_detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.SettingsRepository
import com.example.domain.repository.ShoppingListRepository
import com.example.domain.usecase.GetRecipeByIdUseCase
import com.example.recipe_detail.RecipeDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val shoppingRepository: ShoppingListRepository,
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recipeId: String = checkNotNull(savedStateHandle["recipeId"])

    private val _uiState = MutableStateFlow(RecipeDetailUiState(isLoading = true))
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
}
