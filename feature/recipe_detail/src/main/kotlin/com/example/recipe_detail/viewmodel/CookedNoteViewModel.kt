package com.example.recipe_detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Recipe
import com.example.domain.repository.UserRecipeRepository
import com.example.domain.repository.UserRepository
import com.example.domain.usecase.GetCookedRecipeByIdUseCase
import com.example.domain.usecase.GetRecipeByIdUseCase
import com.example.domain.usecase.UpdateCookedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CookedNoteViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userRecipeRepository: UserRecipeRepository,
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val updateCookedUseCase: UpdateCookedUseCase,
    private val getCookedRecipeByIdUseCase: GetCookedRecipeByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String
        get() = userRepository.getCurrentUser()?.id ?: ""

    private val recipeId: String = savedStateHandle["recipeId"] ?: ""

    private val _uiState = MutableStateFlow(CookedNoteUiState(isLoading = true))
    val uiState: StateFlow<CookedNoteUiState> = _uiState.asStateFlow()

    init {
        loadRecipe()
    }

    private fun loadRecipe() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val isUserRecipe = !recipeId.matches(Regex("\\d+"))
            val recipe: Recipe? = if (isUserRecipe) {
                val userRecipe = userRecipeRepository.getUserRecipeById(userId, recipeId)
                userRecipe?.let {
                    Recipe(
                        id = it.id,
                        name = it.name,
                        cuisine = it.category,
                        imageUrl = it.imagePath ?: "",
                        category = it.category,
                        ingredients = it.ingredients,
                        instructions = it.instructions.split("\n")
                    )
                }
            } else {
                getRecipeByIdUseCase(recipeId)
            }

            val cookedRecipe = getCookedRecipeByIdUseCase(userId, recipeId)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    recipe = recipe,
                    recipeId = recipeId,
                    initialRating = cookedRecipe?.userRating ?: 0,
                    initialNote = cookedRecipe?.userNote ?: ""
                )
            }
        }
    }

    fun saveNote(rating: Int, note: String) {
        viewModelScope.launch {
            updateCookedUseCase(userId, recipeId, rating, note, null)
        }
    }
}

data class CookedNoteUiState(
    val isLoading: Boolean = false,
    val recipe: Recipe? = null,
    val recipeId: String = "",
    val initialRating: Int = 0,
    val initialNote: String = ""
)