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
    val error: String? = null,
    val isShoppingListSyncing: Boolean = false,
    val isShoppingListUpToDate: Boolean = true
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _requiredIngredients = _mealPlanItems.flatMapLatest { items ->
        if (items.isEmpty()) return@flatMapLatest flowOf(emptyList<com.example.domain.model.Ingredient>())
        val userId = authRepository.getCurrentUser()?.id ?: return@flatMapLatest flowOf(emptyList())
        val lang = getCurrentLanguage()

        kotlinx.coroutines.flow.flow {
            val allIngredients = mutableListOf<com.example.domain.model.Ingredient>()
            for (planItem in items) {
                val ingredients = if (planItem.isUserRecipe) {
                    userRecipeRepository.getUserRecipeById(userId, planItem.recipeId)?.ingredients
                } else {
                    recipeRepository.getRecipeById(planItem.recipeId, lang)?.ingredients
                }
                ingredients?.let { allIngredients.addAll(it) }
            }
            emit(allIngredients)
        }
    }

    private val _shoppingList = authRepository.getCurrentUser()?.id?.let { userId ->
        shoppingListRepository.getShoppingList(userId)
    } ?: flowOf(emptyList())

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
        _error,
        _shoppingList,
        _requiredIngredients
    ) { params: Array<Any?> ->
        val currentWeekStart = params[0] as LocalDate
        val mealPlanItems = params[1] as List<MealPlanItem>
        val userRecipes = params[2] as List<UserRecipe>
        val searchResults = params[3] as List<RecipePreview>
        val isLoading = params[4] as Boolean
        val error = params[5] as String?
        val shoppingList = params[6] as List<ShoppingItem>
        val requiredIngredients = params[7] as List<com.example.domain.model.Ingredient>

        PlanUiState(
            currentWeekStart = currentWeekStart,
            mealPlanItems = mealPlanItems,
            userRecipes = userRecipes,
            searchResults = searchResults,
            isLoading = isLoading,
            error = error,
            isShoppingListUpToDate = checkIsShoppingListUpToDate(requiredIngredients, shoppingList)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlanUiState()
    )

    private fun checkIsShoppingListUpToDate(
        requiredIngredients: List<com.example.domain.model.Ingredient>,
        shoppingList: List<ShoppingItem>
    ): Boolean {
        if (requiredIngredients.isEmpty()) return true
        
        val requiredMap = aggregateIngredients(requiredIngredients)
        
        for (reqItem in requiredMap.values) {
            val reqAmt = parseAmount(reqItem.amount)
            
            val activeItems = shoppingList.filter { item ->
                !item.isChecked &&
                item.name.equals(reqItem.name, ignoreCase = true) &&
                item.unit.equals(reqItem.unit, ignoreCase = true)
            }
            
            if (reqAmt != null && reqAmt > 0) {
                val activeAmt = activeItems.sumOf { parseAmount(it.amount) ?: 0.0 }
                if (activeAmt < reqAmt) return false
            } else {
                if (activeItems.isEmpty()) return false
            }
        }
        
        return true
    }

    private fun parseAmount(amountStr: String): Double? {
        val trimmed = amountStr.trim().replace(",", ".")
        if (trimmed.isEmpty()) return null

        val mixedRegex = Regex("""(\d+)\s*[- ]\s*(\d+)/(\d+)""")
        val mixedMatch = mixedRegex.find(trimmed)
        if (mixedMatch != null) {
            val whole = mixedMatch.groupValues[1].toDoubleOrNull() ?: 0.0
            val num = mixedMatch.groupValues[2].toDoubleOrNull() ?: 0.0
            val den = mixedMatch.groupValues[3].toDoubleOrNull() ?: 1.0
            if (den != 0.0) return whole + (num / den)
        }

        if (trimmed.contains("/")) {
            val parts = trimmed.split("/")
            if (parts.size == 2) {
                val num = parts[0].trim().filter { it.isDigit() || it == '.' }.toDoubleOrNull()
                val den = parts[1].trim().filter { it.isDigit() || it == '.' }.toDoubleOrNull()
                if (num != null && den != null && den != 0.0) return num / den
            }
        }

        val regex = Regex("""^([\d.]+)""")
        val match = regex.find(trimmed)
        return match?.groupValues?.get(1)?.toDoubleOrNull()
    }

    private fun formatAmount(amount: Double): String {
        return when {
            amount <= 0 -> ""
            amount % 1.0 == 0.0 -> amount.toInt().toString()
            else -> "%.2f".format(java.util.Locale.US, amount).trimEnd('0').trimEnd('.')
        }
    }

    private fun aggregateIngredients(ingredients: List<com.example.domain.model.Ingredient>): Map<String, ShoppingItem> {
        val userId = authRepository.getCurrentUser()?.id ?: ""
        val aggregated = mutableMapOf<String, ShoppingItem>()

        ingredients.forEach { ingredient ->
            val amt = parseAmount(ingredient.amount)

            val extractedUnit = if (ingredient.unit.isBlank() && amt != null) {
                val trimmedAmount = ingredient.amount.trim()
                val numberPattern = Regex("""^(\d+\s*[- ]\s*\d+/\d+|\d+/\d+|\d+[.,]?\d*)""")
                val match = numberPattern.find(trimmedAmount)
                if (match != null) {
                    trimmedAmount.substring(match.range.last + 1).trim()
                } else ""
            } else {
                ingredient.unit.trim()
            }

            val nameTrimmed = ingredient.name.trim()
            val nameLower = nameTrimmed.lowercase()
            val unitLower = extractedUnit.lowercase()
            val key = "${nameLower}_$unitLower"

            val existing = aggregated[key]
            if (existing != null) {
                if (amt != null) {
                    val currentAmt = parseAmount(existing.amount) ?: 0.0
                    val sum = currentAmt + amt
                    aggregated[key] = existing.copy(
                        amount = formatAmount(sum),
                        unit = extractedUnit
                    )
                }
            } else {
                aggregated[key] = ShoppingItem(
                    id = UUID.randomUUID().toString(),
                    name = nameTrimmed,
                    amount = if (amt != null) formatAmount(amt) else "",
                    unit = if (amt != null) extractedUnit else "",
                    isChecked = false,
                    userId = userId
                )
            }
        }
        return aggregated
    }

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
            try {
                val userId = authRepository.getCurrentUser()?.id ?: return@launch
                val lang = getCurrentLanguage()
                val items = uiState.value.mealPlanItems
                
                val currentShoppingList = shoppingListRepository.getShoppingList(userId).first()
                
                val allPlanIngredients = mutableListOf<com.example.domain.model.Ingredient>()
                for (planItem in items) {
                    val ingredients = if (planItem.isUserRecipe) {
                        userRecipeRepository.getUserRecipeById(userId, planItem.recipeId)?.ingredients
                    } else {
                        recipeRepository.getRecipeById(planItem.recipeId, lang)?.ingredients
                    }
                    ingredients?.let { allPlanIngredients.addAll(it) }
                }
                
                val requiredMap = aggregateIngredients(allPlanIngredients)

                for (reqItem in requiredMap.values) {
                    val reqAmt = parseAmount(reqItem.amount)
                    
                    val activeItems = currentShoppingList.filter { item ->
                        !item.isChecked &&
                        item.name.equals(reqItem.name, ignoreCase = true) &&
                        item.unit.equals(reqItem.unit, ignoreCase = true)
                    }
                    
                    if (reqAmt != null && reqAmt > 0) {
                        val currentActiveAmt = activeItems.sumOf { parseAmount(it.amount) ?: 0.0 }
                        if (currentActiveAmt < reqAmt) {
                            val diff = reqAmt - currentActiveAmt
                            val existingActive = activeItems.firstOrNull()
                            if (existingActive != null) {
                                val oldAmt = parseAmount(existingActive.amount) ?: 0.0
                                val newTotalAmt = oldAmt + diff
                                shoppingListRepository.updateShoppingItem(existingActive.copy(amount = formatAmount(newTotalAmt)))
                            } else {
                                shoppingListRepository.addShoppingItem(reqItem.copy(
                                    id = java.util.UUID.randomUUID().toString(),
                                    amount = formatAmount(diff)
                                ))
                            }
                        }
                    } else {
                        if (activeItems.isEmpty()) {
                            shoppingListRepository.addShoppingItem(reqItem.copy(id = java.util.UUID.randomUUID().toString()))
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
