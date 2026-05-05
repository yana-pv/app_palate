package com.example.recipe_detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val userRecipeRepository: UserRecipeRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String
        get() = userRepository.getCurrentUser()?.id ?: ""

    private val recipeId: String = checkNotNull(savedStateHandle["recipeId"])

    private val _uiState = MutableStateFlow(RecipeDetailUiState(isLoading = true))
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    init {
        loadRecipe()
    }

    fun loadRecipe() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val recipe = getRecipeByIdUseCase(recipeId)
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
    fun addToWantToCook(recipeId: String) {
        viewModelScope.launch {
            val recipe = _uiState.value.recipe ?: return@launch
            userRecipeRepository.addToWantToCook(userId, recipe)
        }
    }

    fun addToShoppingList(recipeId: String) {
        viewModelScope.launch {
            val recipe = _uiState.value.recipe ?: return@launch
            // Здесь будет вызов репозитория для списка покупок
            // userShoppingRepository.addToList(userId, recipe)
        }
    }
}
