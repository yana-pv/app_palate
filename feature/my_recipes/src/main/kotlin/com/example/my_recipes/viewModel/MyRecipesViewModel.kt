package com.example.my_recipes.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.CookedRecipe
import com.example.domain.model.Recipe
import com.example.domain.model.UserRecipe
import com.example.domain.repository.SettingsRepository
import com.example.domain.repository.UserRepository
import com.example.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRecipesViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getWantToCookUseCase: GetWantToCookUseCase,
    private val removeFromWantToCookUseCase: RemoveFromWantToCookUseCase,
    private val getCookedRecipesUseCase: GetCookedRecipesUseCase,
    private val updateCookedUseCase: UpdateCookedUseCase,
    private val removeFromCookedUseCase: RemoveFromCookedUseCase,
    private val getUserRecipesUseCase: GetUserRecipesUseCase,
    private val deleteUserRecipeUseCase: DeleteUserRecipeUseCase,
    private val addToCookedUseCase: AddToCookedUseCase,
    private val addUserRecipeToCookedUseCase: AddUserRecipeToCookedUseCase,
    private val settingsRepository: SettingsRepository

) : ViewModel() {

    private val userId: String
        get() = userRepository.getCurrentUser()?.id ?: ""

    private val _uiState = MutableStateFlow(MyRecipesUiState())
    val uiState: StateFlow<MyRecipesUiState> = _uiState.asStateFlow()

    init {
        loadData()
        observeTheme()
    }

    private fun loadData() {
        viewModelScope.launch {
            getWantToCookUseCase(userId).collectLatest { wantToCook ->
                _uiState.update { it.copy(wantToCook = wantToCook) }
            }
        }

        viewModelScope.launch {
            getCookedRecipesUseCase(userId).collectLatest { cooked ->
                _uiState.update { it.copy(cooked = cooked) }
            }
        }

        viewModelScope.launch {
            getUserRecipesUseCase(userId).collectLatest { userRecipes ->
                _uiState.update { it.copy(userRecipes = userRecipes) }
            }
        }
    }

    fun moveToCooked(recipeId: String) {
        viewModelScope.launch {
            val recipe = _uiState.value.wantToCook.find { it.id == recipeId }
            if (recipe != null) {
                addToCookedUseCase(userId, recipe, 5, "", null)
                removeFromWantToCookUseCase(userId, recipeId)
            }
        }
    }

    fun removeFromWantToCook(recipeId: String) {
        viewModelScope.launch {
            removeFromWantToCookUseCase(userId, recipeId)
        }
    }

    fun removeFromCooked(recipeId: String) {
        viewModelScope.launch {
            removeFromCookedUseCase(userId, recipeId)
        }
    }

    fun deleteUserRecipe(recipeId: String) {
        viewModelScope.launch {
            deleteUserRecipeUseCase(userId, recipeId)
        }
    }

    fun updateCookedNote(recipeId: String, note: String) {
        viewModelScope.launch {
            val current = _uiState.value.cooked.find { it.recipeId == recipeId }
            current?.let {
                updateCookedUseCase(userId, recipeId, it.userRating, note, it.userPhotoPath)
            }
        }
    }

    fun moveUserRecipeToCooked(recipe: UserRecipe) {
        viewModelScope.launch {
            addUserRecipeToCookedUseCase(userId, recipe, 5, "", null)
        }
    }


    private fun observeTheme() {
        viewModelScope.launch {
            settingsRepository.isDarkMode().collect { isDark ->
                _uiState.update { it.copy(isDarkMode = isDark) }
            }
        }
    }
}

data class MyRecipesUiState(
    val wantToCook: List<Recipe> = emptyList(),
    val cooked: List<CookedRecipe> = emptyList(),
    val userRecipes: List<UserRecipe> = emptyList(),
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false
)