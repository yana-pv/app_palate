package com.example.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Category
import com.example.domain.model.RecipePreview
import com.example.domain.repository.UserRecipeRepository
import com.example.domain.repository.UserRepository
import com.example.domain.usecase.GetHomeDataUseCase
import com.example.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val settingsRepository: com.example.domain.repository.SettingsRepository,
    private val userRecipeRepository: UserRecipeRepository,
    private val userRepository: UserRepository  // ← добавить

) : ViewModel() {

    private val userId: String
        get() = userRepository.getCurrentUser()?.id ?: ""

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var allRecipes: List<RecipePreview> = emptyList()
    private var allCategories: List<Category> = emptyList()
    private val allCategory = Category(id = "all", name = "Все", imageUrl = "")

    init {
        loadHomeData()
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.isDarkMode().collect { isDark ->
                _uiState.update { it.copy(isDarkMode = isDark) }
            }
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val homeData = getHomeDataUseCase()
                allCategories = listOf(allCategory) + homeData.categories
                allRecipes = homeData.recipesByCategory.values.flatten()
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        categories = allCategories,
                        cuisines = homeData.cuisines,
                        recipes = allRecipes,
                        errorMessage = if (allRecipes.isEmpty()) "No data available" else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Network error. Showing cached data." 
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onCategorySelected(categoryId: String) {
        _uiState.update { state ->
            val currentSelected = state.selectedCategoryIds.toMutableSet()
            if (categoryId == "all") {
                currentSelected.clear()
                currentSelected.add("all")
            } else {
                currentSelected.remove("all")
                if (currentSelected.contains(categoryId)) {
                    currentSelected.remove(categoryId)
                } else {
                    currentSelected.add(categoryId)
                }
                if (currentSelected.isEmpty()) currentSelected.add("all")
            }
            state.copy(selectedCategoryIds = currentSelected)
        }
        applyFilters()
    }

    fun onCuisineSelected(cuisine: String) {
        _uiState.update { state ->
            val currentSelected = state.selectedCuisines.toMutableSet()
            if (cuisine == "null") {
                currentSelected.clear()
            } else {
                if (currentSelected.contains(cuisine)) {
                    currentSelected.remove(cuisine)
                } else {
                    currentSelected.add(cuisine)
                }
            }
            state.copy(selectedCuisines = currentSelected)
        }
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        val selectedCategoryNames = allCategories
            .filter { it.id in state.selectedCategoryIds && it.id != "all" }
            .map { it.name.lowercase() }
        
        val filteredRecipes = allRecipes.filter { recipe ->
            val matchesQuery = recipe.name.contains(state.searchQuery, ignoreCase = true)
            val matchesCategory = state.selectedCategoryIds.contains("all") || 
                                 selectedCategoryNames.any { it == recipe.categoryName.lowercase() }
            val matchesCuisine = state.selectedCuisines.isEmpty() || 
                                state.selectedCuisines.any { cuisine -> recipe.name.contains(cuisine, ignoreCase = true) }
            
            matchesQuery && matchesCategory && matchesCuisine
        }
        
        _uiState.update { it.copy(recipes = filteredRecipes) }
    }

    fun setFilterSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(isFilterSheetVisible = visible) }
    }

    fun resetFilters() {
        _uiState.update { 
            it.copy(
                searchQuery = "", 
                selectedCategoryIds = setOf("all"), 
                selectedCuisines = emptySet(),
                isFilterSheetVisible = false
            ) 
        }
        if (allRecipes.isEmpty()) {
            loadHomeData()
        } else {
            applyFilters()
        }
    }

   /* fun toggleSaveRecipe(recipeId: String) {
        // Найти рецепт по ID
        val recipe = allRecipes.find { it.id == recipeId } ?: return

        viewModelScope.launch {
            // Проверить, есть ли уже в "Хочу приготовить"
            val isSaved = userRecipeRepository.isInWantToCook(recipeId)

            if (isSaved) {
                // Удалить из "Хочу приготовить"
                userRecipeRepository.removeFromWantToCook(recipeId)
            } else {
                // Получить полный рецепт (с ингредиентами)
                // Временно создаем Recipe из RecipePreview
                val fullRecipe = com.example.domain.model.Recipe(
                    id = recipe.id,
                    name = recipe.name,
                    cuisine = "",
                    imageUrl = recipe.imageUrl,
                    category = recipe.categoryName,
                    ingredients = emptyList(),
                    instructions = emptyList()
                )
                userRecipeRepository.addToWantToCook(fullRecipe)
            }

            // Обновить UI
            val updatedRecipes = _uiState.value.recipes.map {
                if (it.id == recipeId) it.copy(isSaved = !isSaved) else it
            }
            allRecipes = allRecipes.map {
                if (it.id == recipeId) it.copy(isSaved = !isSaved) else it
            }
            _uiState.update { it.copy(recipes = updatedRecipes) }
        }
    }*/

    fun toggleSaveRecipe(recipeId: String) {
        // Найти рецепт по ID
        val recipe = allRecipes.find { it.id == recipeId } ?: return

        viewModelScope.launch {
            // Проверить, есть ли уже в "Хочу приготовить"
            val isSaved = userRecipeRepository.isInWantToCook(userId, recipeId)

            if (isSaved) {
                // Удалить из "Хочу приготовить"
                userRecipeRepository.removeFromWantToCook(userId, recipeId)
            } else {
                // Получить полный рецепт (с ингредиентами)
                val fullRecipe = com.example.domain.model.Recipe(
                    id = recipe.id,
                    name = recipe.name,
                    cuisine = "",
                    imageUrl = recipe.imageUrl,
                    category = recipe.categoryName,
                    ingredients = emptyList(),
                    instructions = emptyList()
                )
                userRecipeRepository.addToWantToCook(userId, fullRecipe)
            }

            // Обновить UI
            val updatedRecipes = _uiState.value.recipes.map {
                if (it.id == recipeId) it.copy(isSaved = !isSaved) else it
            }
            allRecipes = allRecipes.map {
                if (it.id == recipeId) it.copy(isSaved = !isSaved) else it
            }
            _uiState.update { it.copy(recipes = updatedRecipes) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }



 /*   fun toggleSaveRecipe(recipeId: String) {
        val updatedRecipes = _uiState.value.recipes.map {
            if (it.id == recipeId) it.copy(isSaved = !it.isSaved) else it
        }
        allRecipes = allRecipes.map {
            if (it.id == recipeId) it.copy(isSaved = !it.isSaved) else it
        }
        _uiState.update { it.copy(recipes = updatedRecipes) }
    }

*/
}
