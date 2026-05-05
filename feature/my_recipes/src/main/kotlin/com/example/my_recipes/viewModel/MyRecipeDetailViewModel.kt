package com.example.my_recipes.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Recipe
import com.example.domain.model.UserRecipe
import com.example.domain.repository.UserRecipeRepository
import com.example.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRecipeDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userRecipeRepository: UserRecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String
        get() = userRepository.getCurrentUser()?.id ?: ""

    private val recipeId: String = savedStateHandle["recipeId"] ?: ""

    private val _uiState = MutableStateFlow(MyRecipeDetailUiState(isLoading = true))
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
                    errorMessage = if (recipe == null) "Рецепт не найден" else null
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
}

data class MyRecipeDetailUiState(
    val isLoading: Boolean = false,
    val recipe: UserRecipe? = null,
    val errorMessage: String? = null
)