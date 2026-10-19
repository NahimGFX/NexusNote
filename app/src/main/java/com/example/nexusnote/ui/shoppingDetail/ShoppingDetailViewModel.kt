package com.example.nexusnote.ui.shoppingDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexusnote.data.local.entity.ShoppingListItemEntity
import com.example.nexusnote.data.repository.ProductRepository
import com.example.nexusnote.data.repository.ShoppingListItemRepository
import com.example.nexusnote.data.repository.ShoppingListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingDetailViewModel @Inject constructor(
    private val itemRepository: ShoppingListItemRepository,
    private val productRepository: ProductRepository,
    private val listRepository: ShoppingListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ShoppingDetailUiState(
            listName = "",
            isLoading = true
        )
    )

    val uiState: StateFlow<ShoppingDetailUiState> =
        _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var nameJob: Job? = null

    fun loadList(listId: Int) {
        loadJob?.cancel()
        nameJob?.cancel()

        _uiState.update {
            it.copy(
                listId = listId,
                listName = "",
                products = emptyList(),
                isLoading = true,
                errorMessage = null
            )
        }

        // Consultar el nombre real de la lista
        nameJob = viewModelScope.launch {
            listRepository.getListById(listId)
                .catch { error ->
                    showError(error)
                }
                .collect { list ->
                    _uiState.update {
                        it.copy(
                            listName = list?.name
                                ?: "Lista no encontrada"
                        )
                    }
                }
        }

        // Consultar sus productos desde Room
        loadJob = viewModelScope.launch {
            itemRepository.getItemsByList(listId)
                .catch { error ->
                    showError(error)
                }
                .collect { items ->
                    try {
                        val products = items.map { item ->
                            val product = productRepository
                                .getProductById(item.productId)

                            ShoppingDetailProduct(
                                id = item.id,
                                name = product?.name
                                    ?: "Producto no disponible",
                                quantity = item.quantity,
                                unit = "unidades",
                                emoji = "🛒",
                                purchased = item.isPurchased,
                                photoUri = product?.photoUri
                            )
                        }

                        _uiState.update {
                            it.copy(
                                products = products,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    } catch (error: Exception) {
                        showError(error)
                    }
                }
        }
    }

    fun togglePurchased(itemId: Int) {
        val listId = _uiState.value.listId

        viewModelScope.launch {
            try {
                val item = findItem(listId, itemId)
                    ?: return@launch

                itemRepository.updateItem(
                    item.copy(
                        isPurchased = !item.isPurchased
                    )
                )
            } catch (error: Exception) {
                showError(error)
            }
        }
    }

    fun updateQuantity(itemId: Int, newQuantity: Int) {
        if (newQuantity < 1) return

        val listId = _uiState.value.listId

        viewModelScope.launch {
            try {
                val item = findItem(listId, itemId)
                    ?: return@launch

                itemRepository.updateItem(
                    item.copy(quantity = newQuantity)
                )
            } catch (error: Exception) {
                showError(error)
            }
        }
    }

    fun deleteProduct(itemId: Int) {
        val listId = _uiState.value.listId

        viewModelScope.launch {
            try {
                val item = findItem(listId, itemId)
                    ?: return@launch

                itemRepository.deleteItem(item.id)
            } catch (error: Exception) {
                showError(error)
            }
        }
    }

    private suspend fun findItem(
        listId: Int,
        itemId: Int
    ): ShoppingListItemEntity? {
        return itemRepository
            .getItemsByList(listId)
            .first()
            .find { it.id == itemId }
    }

    private fun showError(error: Throwable) {
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = error.message
                    ?: "Ocurrió un error al procesar la compra"
            )
        }
    }
}
