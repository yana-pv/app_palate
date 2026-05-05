package com.example.create_recipe

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Ingredient
import com.example.domain.model.UserRecipe
import com.example.domain.repository.UserRecipeRepository
import com.example.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import android.content.ContentResolver
import com.example.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltViewModel
class CreateRecipeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userRecipeRepository: UserRecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recipeId: String? = savedStateHandle["recipeId"]

    init {
        if (recipeId != null) {
            loadRecipeForEdit()
        }
    }



    private val userId: String
        get() = userRepository.getCurrentUser()?.id ?: ""

    private val _uiState = MutableStateFlow(CreateRecipeUiState())
    val uiState: StateFlow<CreateRecipeUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateCuisine(cuisine: String) {
        _uiState.update { it.copy(cuisine = cuisine) }
    }

    fun updateCategory(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    fun updateInstructions(instructions: String) {
        _uiState.update { it.copy(instructions = instructions) }
    }

    fun updateImageUri(uri: String?) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    fun updateIngredient(index: Int, name: String, amount: String, unit: String) {
        _uiState.update { state ->
            val newIngredients = state.ingredients.toMutableList()
            newIngredients[index] = Ingredient(name, amount, unit)
            state.copy(ingredients = newIngredients)
        }
    }

    fun addIngredient() {
        _uiState.update { state ->
            state.copy(ingredients = state.ingredients + Ingredient("", "", ""))
        }
    }

    fun removeIngredient(index: Int) {
        _uiState.update { state ->
            if (state.ingredients.size > 1) {
                val newIngredients = state.ingredients.toMutableList()
                newIngredients.removeAt(index)
                state.copy(ingredients = newIngredients)
            } else {
                state.copy(ingredients = listOf(Ingredient("", "", "")))
            }
        }
    }

    fun saveRecipe(contentResolver: ContentResolver) {
        val state = _uiState.value

        // Валидация
        if (state.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Введите название рецепта") }
            return
        }

        val validIngredients = state.ingredients.filter { it.name.isNotBlank() }
        if (validIngredients.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Добавьте хотя бы один ингредиент") }
            return
        }

        if (state.instructions.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Введите инструкцию приготовления") }
            return
        }



        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val finalRecipeId = if (recipeId.isNullOrBlank()) UUID.randomUUID().toString() else recipeId
            var finalImageUrl = state.imageUri

            if (state.imageUri != null && (state.imageUri.startsWith("content://") || state.imageUri.startsWith("file://"))) {
                val bytes = withContext(Dispatchers.IO) {
                    try {
                        contentResolver.openInputStream(Uri.parse(state.imageUri))?.use { it.readBytes() }
                    } catch (e: Exception) {
                        null
                    }
                }

                if (bytes != null) {
                    val uploadResult = userRepository.uploadRecipeImage(bytes, finalRecipeId)
                    if (uploadResult is Resource.Success) {
                        finalImageUrl = uploadResult.data
                    } else if (uploadResult is Resource.Error) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Ошибка загрузки фото: ${uploadResult.message}") }
                        return@launch
                    }
                }
            }

            val userRecipe = UserRecipe(
                id = if (recipeId.isNullOrBlank()) UUID.randomUUID().toString() else recipeId,
                name = state.name,
                imagePath = finalImageUrl,
                category = state.category.ifBlank { "Общее" },
                cookingTime = 0,
                ingredients = validIngredients,
                instructions = state.instructions,
                createdAt = System.currentTimeMillis()
            )

            if (recipeId.isNullOrBlank()) {
                // Создание нового рецепта
                userRecipeRepository.saveUserRecipe(userId, userRecipe)
            } else {
                // Обновление существующего рецепта
                userRecipeRepository.updateUserRecipe(userId, userRecipe)
            }

            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadRecipeForEdit() {
        viewModelScope.launch {
            val recipe = userRecipeRepository.getUserRecipeById(userId, recipeId!!)
            recipe?.let {
                _uiState.update { state ->
                    state.copy(
                        name = it.name,
                        cuisine = it.category,
                        category = it.category,
                        ingredients = it.ingredients.map { ingredient ->
                            Ingredient(
                                name = ingredient.name,
                                amount = ingredient.amount,
                                unit = ingredient.unit
                            )
                        },
                        instructions = it.instructions,
                        imageUri = it.imagePath
                    )
                }
            }
        }
    }

}