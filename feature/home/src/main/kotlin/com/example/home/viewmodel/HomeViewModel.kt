package com.example.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Category
import com.example.domain.model.RecipePreview
import com.example.domain.usecase.GetHomeDataUseCase
import com.example.domain.usecase.GetRecipesByCuisineUseCase
import com.example.domain.repository.SettingsRepository
import com.example.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val getRecipesByCuisineUseCase: GetRecipesByCuisineUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var allRecipes: List<RecipePreview> = emptyList()
    private var allCategories: List<Category> = emptyList()
    
    private val allCategory = Category(id = "all", name = "", imageUrl = "", originalName = "All")

    private var settingsJob: Job? = null
    private var loadingJob: Job? = null

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.isDarkMode().collect { isDark ->
                _uiState.update { it.copy(isDarkMode = isDark) }
            }
        }
        
        settingsJob?.cancel()
        settingsJob = viewModelScope.launch {
            settingsRepository.getLanguage().collect { language ->
                loadHomeData(language)
            }
        }
    }

    fun loadHomeData(language: String = "en") {
        loadingJob?.cancel()
        loadingJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                getHomeDataUseCase(language).collect { homeData ->
                    allCategories = listOf(allCategory) + homeData.categories
                    
                    val newRecipes = homeData.recipesByCategory.values.flatten()
                    if (newRecipes.isNotEmpty() || homeData.categories.isEmpty()) {
                        val currentRecipesMap = allRecipes.associateBy { it.id }.toMutableMap()
                        newRecipes.forEach { newRecipe ->
                            val existing = currentRecipesMap[newRecipe.id]
                            currentRecipesMap[newRecipe.id] = if (existing != null) {
                                newRecipe.copy(cuisine = newRecipe.cuisine.ifEmpty { existing.cuisine })
                            } else newRecipe
                        }
                        allRecipes = currentRecipesMap.values.toList()
                        
                        _uiState.update { state ->
                            val isStillLoading = homeData.recipesByCategory.isEmpty() || 
                                (homeData.recipesByCategory.size < homeData.categories.take(8).size && homeData.categories.isNotEmpty())
                            
                            state.copy(
                                isLoading = isStillLoading && allRecipes.isEmpty(),
                                categories = allCategories,
                                cuisines = homeData.cuisines,
                                errorMessage = null
                            )
                        }
                        applyFilters()
                    } else if (homeData.categories.isNotEmpty() && allRecipes.isEmpty()) {
                         _uiState.update { it.copy(categories = allCategories, cuisines = homeData.cuisines) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
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
                if (!currentSelected.add(categoryId)) {
                    currentSelected.remove(categoryId)
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
            if (cuisine in listOf("all", "null", "Any", "Все")) {
                currentSelected.clear()
            } else {
                if (!currentSelected.add(cuisine)) {
                    currentSelected.remove(cuisine)
                }
            }
            state.copy(selectedCuisines = currentSelected)
        }
        
        val selectedCuisines = _uiState.value.selectedCuisines
        if (selectedCuisines.isNotEmpty()) {
            viewModelScope.launch {
                val language = settingsRepository.getLanguage().first()
                selectedCuisines.forEach { cuisineName ->
                    try {
                        val newRecipes = getRecipesByCuisineUseCase(cuisineName, language)
                        val currentRecipesMap = allRecipes.associateBy { it.id }.toMutableMap()
                        
                        newRecipes.forEach { newRecipe ->
                            val existing = currentRecipesMap[newRecipe.id]
                            currentRecipesMap[newRecipe.id] = if (existing != null) {
                                existing.copy(cuisine = cuisineName)
                            } else newRecipe.copy(cuisine = cuisineName)
                        }
                        
                        allRecipes = currentRecipesMap.values.toList()
                        applyFilters()
                    } catch (e: Exception) { }
                }
            }
        }
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        val filteredRecipes = allRecipes.filter { recipe ->
            val matchesQuery = recipe.name.contains(state.searchQuery, ignoreCase = true) ||
                recipe.originalName.contains(state.searchQuery, ignoreCase = true)
            
            val matchesCategory = state.selectedCategoryIds.contains("all") ||
                state.selectedCategoryIds.contains(recipe.categoryId)

            val matchesCuisine = state.selectedCuisines.isEmpty() ||
                state.selectedCuisines.contains(recipe.cuisine)
            
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
        if (allRecipes.isEmpty()) loadHomeData() else applyFilters()
    }

    fun toggleSaveRecipe(recipeId: String) {
        val toggle: (RecipePreview) -> RecipePreview = { 
            if (it.id == recipeId) it.copy(isSaved = !it.isSaved) else it 
        }
        allRecipes = allRecipes.map(toggle)
        _uiState.update { it.copy(recipes = it.recipes.map(toggle)) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
