package com.example.shopping_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.ShoppingItem
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.ShoppingListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShoppingListUiState(
    val items: List<ShoppingItem> = emptyList(),
    val isLoading: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val editingItem: ShoppingItem? = null
)

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val repository: ShoppingListRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShoppingListUiState())
    val uiState: StateFlow<ShoppingListUiState> = _uiState.asStateFlow()

    init {
        loadShoppingList()
    }

    private fun loadShoppingList() {
        val userId = authRepository.getCurrentUser()?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getShoppingList(userId).collect { items ->
                val sortedItems = items.sortedWith(
                    compareBy<ShoppingItem> { it.isChecked }
                        .thenBy { it.name }
                )
                _uiState.update { it.copy(items = sortedItems, isLoading = false) }
            }
        }
    }

    fun addItem(name: String, amount: String, unit: String) {
        val userId = authRepository.getCurrentUser()?.id ?: return
        if (name.isBlank()) return
        
        viewModelScope.launch {
            repository.addOrUpdateShoppingItem(
                ShoppingItem(
                    name = name,
                    amount = amount,
                    unit = unit,
                    userId = userId
                )
            )
        }
    }

    fun toggleItemChecked(item: ShoppingItem) {
        viewModelScope.launch {
            repository.updateShoppingItem(item.copy(isChecked = !item.isChecked))
        }
    }

    fun updateItem(id: String, name: String, amount: String, unit: String) {
        val userId = authRepository.getCurrentUser()?.id ?: return
        viewModelScope.launch {
            repository.updateShoppingItem(
                ShoppingItem(
                    id = id,
                    name = name,
                    amount = amount,
                    unit = unit,
                    userId = userId,
                    isChecked = _uiState.value.items.find { it.id == id }?.isChecked ?: false
                )
            )
            _uiState.update { it.copy(editingItem = null) }
        }
    }

    fun deleteCheckedItems() {
        val idsToDelete = _uiState.value.items.filter { it.isChecked }.map { it.id }
        if (idsToDelete.isEmpty()) return
        
        viewModelScope.launch {
            repository.deleteShoppingItems(idsToDelete)
            _uiState.update { it.copy(showDeleteConfirmation = false) }
        }
    }

    fun showDeleteConfirmation(show: Boolean) {
        _uiState.update { it.copy(showDeleteConfirmation = show) }
    }

    fun startEditing(item: ShoppingItem?) {
        _uiState.update { it.copy(editingItem = item) }
    }
}
