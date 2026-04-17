package com.example.palate.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Category
import com.example.domain.model.RecipePreview
import com.example.domain.usecase.GetHomeDataUseCase
import com.example.home.HomeUiState
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
                    selectedCategoryIds = setOf("all")
                )
            }

            catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val currentState = _uiState.value as? HomeUiState.Success ?: return
        filterData(query, currentState.selectedCategoryIds, currentState.selectedCuisines)
    }

    fun onCategorySelected(categoryId: String) {
        val currentState = _uiState.value as? HomeUiState.Success ?: return
        val currentSelected = currentState.selectedCategoryIds.toMutableSet()

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
            if (currentSelected.isEmpty()) {
                currentSelected.add("all")
            }
        }

        filterData(currentState.searchQuery, currentSelected, currentState.selectedCuisines)
    }

    fun onCuisineSelected(cuisine: String) {
        val currentState = _uiState.value as? HomeUiState.Success ?: return
        val currentSelected = currentState.selectedCuisines.toMutableSet()

        if (cuisine == "null") {
            currentSelected.clear()
        } else {
            if (currentSelected.contains(cuisine)) {
                currentSelected.remove(cuisine)
            } else {
                currentSelected.add(cuisine)
            }
        }

        filterData(currentState.searchQuery, currentState.selectedCategoryIds, currentSelected)
    }

    fun setFilterSheetVisible(visible: Boolean) {
        val currentState = _uiState.value as? HomeUiState.Success ?: return
        _uiState.value = currentState.copy(isFilterSheetVisible = visible)
    }

    fun resetFilters() {
        filterData("", setOf("all"), emptySet())
        setFilterSheetVisible(false)
    }

    private fun filterData(query: String, categoryIds: Set<String>, cuisines: Set<String>) {
        val currentState = _uiState.value as? HomeUiState.Success ?: return
        
        val selectedCategoryNames = allCategories
            .filter { it.id in categoryIds && it.id != "all" }
            .map { it.name.lowercase() }
        
        val filteredRecipes = allRecipes.filter { recipe ->
            val matchesQuery = recipe.name.contains(query, ignoreCase = true)
            
            val matchesCategory = categoryIds.contains("all") || 
                                 selectedCategoryNames.any { it == recipe.categoryName.lowercase() }
            
            val matchesCuisine = cuisines.isEmpty() || 
                                cuisines.any { cuisine -> recipe.name.contains(cuisine, ignoreCase = true) }
            
            matchesQuery && matchesCategory && matchesCuisine
        }
        
        _uiState.value = currentState.copy(
            recipes = filteredRecipes,
            searchQuery = query,
            selectedCategoryIds = categoryIds,
            selectedCuisines = cuisines
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