package com.example.palate.feature.recipe_detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.repository.RecipeRepository
import com.example.palate.feature.recipe_detail.RecipeDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recipeId: String = checkNotNull(savedStateHandle["recipeId"])

    private val _uiState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    init {
        loadRecipe()
    }

    fun loadRecipe() {
        viewModelScope.launch {
            _uiState.value = RecipeDetailUiState.Loading
            try {
                val recipe = repository.getRecipeById(recipeId)
                if (recipe != null) {
                    _uiState.value = RecipeDetailUiState.Success(recipe)
                } else {
                    _uiState.value = RecipeDetailUiState.Error("Recipe not found")
                }
            } catch (e: Exception) {
                _uiState.value = RecipeDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
