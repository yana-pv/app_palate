package com.example.palate.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Category
import com.example.domain.model.RecipePreview
import com.example.domain.usecase.GetHomeDataUseCase
import com.example.palate.feature.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    private var allRecipes: List<RecipePreview> = emptyList()
    private var allCategories: List<Category> = emptyList()

    private val allCategory = Category(id = "all", name = "Все", imageUrl = "")

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val homeData = getHomeDataUseCase()
                allCategories = listOf(allCategory) + homeData.categories
                allRecipes = homeData.recipesByCategory.values.flatten()
                
                _uiState.value = HomeUiState.Success(
                    categories = allCategories,
                    cuisines = homeData.cuisines,
                    recipes = allRecipes,
                    selectedCategoryId = "all"
                )
            }

            catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val currentState = _uiState.value as? HomeUiState.Success ?: return
        filterData(query, currentState.selectedCategoryId, currentState.selectedCuisine)
    }

    fun onCategorySelected(categoryId: String?) {
        val currentState = _uiState.value as? HomeUiState.Success ?: return
        filterData(currentState.searchQuery, categoryId, currentState.selectedCuisine)
    }

    fun onCuisineSelected(cuisine: String?) {
        val currentState = _uiState.value as? HomeUiState.Success ?: return
        filterData(currentState.searchQuery, currentState.selectedCategoryId, cuisine)
    }

    fun setFilterSheetVisible(visible: Boolean) {
        val currentState = _uiState.value as? HomeUiState.Success ?: return
        _uiState.value = currentState.copy(isFilterSheetVisible = visible)
    }

    fun resetFilters() {
        filterData("", "all", null)
        setFilterSheetVisible(false)
    }

    private fun filterData(query: String, categoryId: String?, cuisine: String?) {
        val currentState = _uiState.value as? HomeUiState.Success ?: return
        
        val targetCategoryName = allCategories.find { it.id == categoryId }?.name
        
        val filteredRecipes = allRecipes.filter { recipe ->
            val matchesQuery = recipe.name.contains(query, ignoreCase = true)
            val matchesCategory = categoryId == null || categoryId == "all" || 
                                 recipe.categoryName.equals(targetCategoryName, ignoreCase = true)
            val matchesCuisine = cuisine == null || recipe.name.contains(cuisine, ignoreCase = true)
            
            matchesQuery && matchesCategory && matchesCuisine
        }
        
        _uiState.value = currentState.copy(
            recipes = filteredRecipes,
            searchQuery = query,
            selectedCategoryId = categoryId,
            selectedCuisine = cuisine
        )
    }

    fun toggleSaveRecipe(recipeId: String) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            val updatedRecipes = currentState.recipes.map {
                if (it.id == recipeId) it.copy(isSaved = !it.isSaved) else it
            }
            allRecipes = allRecipes.map {
                if (it.id == recipeId) it.copy(isSaved = !it.isSaved) else it
            }
            _uiState.value = currentState.copy(recipes = updatedRecipes)
        }
    }
}