package com.example.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.MealPlanItem
import com.example.domain.model.MealType
import com.example.domain.model.RecipePreview
import com.example.domain.model.ShoppingItem
import com.example.domain.model.UserRecipe
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.MealPlanRepository
import com.example.domain.repository.RecipeRepository
import com.example.domain.repository.ShoppingListRepository
import com.example.domain.repository.UserRecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.UUID
import javax.inject.Inject

data class PlanUiState(
    val currentWeekStart: LocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)),
    val mealPlanItems: List<MealPlanItem> = emptyList(),
    val userRecipes: List<UserRecipe> = emptyList(),
    val searchResults: List<RecipePreview> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val mealPlanRepository: MealPlanRepository,
    private val authRepository: AuthRepository,
    private val recipeRepository: RecipeRepository,
    private val userRecipeRepository: UserRecipeRepository,
    private val shoppingListRepository: ShoppingListRepository,
    private val settingsRepository: com.example.domain.repository.SettingsRepository
) : ViewModel() {

    private suspend fun getCurrentLanguage(): String = settingsRepository.getLanguage().first()

    private val _currentWeekStart = MutableStateFlow<LocalDate>(LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)))
    
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _mealPlanItems = _currentWeekStart.flatMapLatest { date ->
        val userId = authRepository.getCurrentUser()?.id ?: return@flatMapLatest flowOf(emptyList<MealPlanItem>())
        mealPlanRepository.getMealPlanForWeek(userId, date)
    }

    private val _userRecipes = MutableStateFlow<List<UserRecipe>>(emptyList())
    private val _searchResults = MutableStateFlow<List<RecipePreview>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PlanUiState> = combine(
        _currentWeekStart,
        _mealPlanItems,
        _userRecipes,
        _searchResults,
        _isLoading,
        _error
    ) { params ->
        PlanUiState(
            currentWeekStart = params[0] as LocalDate,
            mealPlanItems = params[1] as List<MealPlanItem>,
            userRecipes = params[2] as List<UserRecipe>,
            searchResults = params[3] as List<RecipePreview>,
            isLoading = params[4] as Boolean,
            error = params[5] as String?
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlanUiState()
    )

    init {
        loadUserRecipes()
    }

    private fun loadUserRecipes() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.id ?: return@launch
            userRecipeRepository.getUserRecipes(userId).collectLatest { recipes ->
                _userRecipes.value = recipes
            }
        }
    }

    fun nextWeek() {
        _currentWeekStart.update { it.plusWeeks(1) }
    }

    fun previousWeek() {
        _currentWeekStart.update { it.minusWeeks(1) }
    }

    fun addMeal(date: LocalDate, mealType: MealType, recipeId: String, isUserRecipe: Boolean) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.id ?: return@launch
            val lang = getCurrentLanguage()
            
            val (name, imageUrl, category) = if (isUserRecipe) {
                val recipe = userRecipeRepository.getUserRecipeById(userId, recipeId)
                Triple(recipe?.name ?: "", recipe?.imagePath, recipe?.category ?: "")
            } else {
                val recipe = recipeRepository.getRecipeById(recipeId, lang)
                Triple(recipe?.name ?: "", recipe?.imageUrl, recipe?.category ?: "")
            }

            val item = MealPlanItem(
                id = "${userId}:::${date}:::${mealType}",
                userId = userId,
                date = date,
                mealType = mealType,
                recipeId = recipeId,
                recipeName = name,
                recipeImageUrl = imageUrl,
                recipeCategory = category,
                isUserRecipe = isUserRecipe
            )
            mealPlanRepository.addMealPlanItem(item)
        }
    }

    fun removeMeal(itemId: String) {
        viewModelScope.launch {
            mealPlanRepository.removeMealPlanItem(itemId)
        }
    }

    fun assembleShoppingList() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = authRepository.getCurrentUser()?.id ?: return@launch
            val items = uiState.value.mealPlanItems
            
            val aggregatedIngredients = mutableMapOf<String, ShoppingItem>()

            items.forEach { planItem ->
                val lang = getCurrentLanguage()
                val ingredients = if (planItem.isUserRecipe) {
                    userRecipeRepository.getUserRecipeById(userId, planItem.recipeId)?.ingredients ?: emptyList()
                } else {
                    recipeRepository.getRecipeById(planItem.recipeId, lang)?.ingredients ?: emptyList()
                }
                
                ingredients.forEach { ingredient ->
                    val key = "${ingredient.name.lowercase()}_${ingredient.unit.lowercase()}"
                    val existing = aggregatedIngredients[key]
                    if (existing != null) {
                        val currentAmount = existing.amount.toDoubleOrNull() ?: 0.0
                        val newAmount = currentAmount + (ingredient.amount.toDoubleOrNull() ?: 0.0)
                        aggregatedIngredients[key] = existing.copy(
                            amount = if (newAmount % 1.0 == 0.0) newAmount.toInt().toString() else newAmount.toString()
                        )
                    } else {
                        aggregatedIngredients[key] = ShoppingItem(
                            id = UUID.randomUUID().toString(),
                            name = ingredient.name,
                            amount = ingredient.amount,
                            unit = ingredient.unit,
                            isChecked = false,
                            userId = userId
                        )
                    }
                }
            }

            aggregatedIngredients.values.forEach { item ->
                shoppingListRepository.addOrUpdateShoppingItem(item)
            }
            _isLoading.value = false
        }
    }
}
