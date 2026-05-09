package com.example.my_recipes.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Recipe
import com.example.domain.model.UserRecipe
import com.example.domain.repository.MealPlanRepository
import com.example.domain.repository.ShoppingListRepository
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
    private val shoppingRepository: ShoppingListRepository,
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
            if (recipe != null) {
                val isInWantToCook = userRecipeRepository.isInWantToCook(userId, recipe.id)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        recipe = recipe,
                        isInWantToCook = isInWantToCook,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        recipe = null,
                        errorMessage = "Recipe not found"
                    )
                }
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
            _uiState.update { it.copy(isInWantToCook = true) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun dismissAlreadyAddedDialog() {
        _uiState.update { it.copy(showAlreadyAddedDialog = false) }
    }

    fun addIngredientsToShoppingList(force: Boolean = false) {
        val recipe = _uiState.value.recipe ?: return
        val currentUserId = userId

        viewModelScope.launch {
            if (!force) {
                val alreadyAdded = shoppingRepository.isRecipeIngredientsAlreadyAdded(
                    userId = currentUserId,
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
                    userId = currentUserId
                )
            }

            try {
                shoppingRepository.addIngredientsToShoppingList(currentUserId, recipe.id, items)
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
    val successMessage: String? = null,
    val isSelectionMode: Boolean = false,
    val isSelected: Boolean = false,
    val isInWantToCook: Boolean = false,
    val showAlreadyAddedDialog: Boolean = false
)