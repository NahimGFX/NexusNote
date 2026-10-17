package com.example.nexusnote.ui.shoppingDetail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ShoppingDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        ShoppingDetailUiState(
            listId = 1,
            products = listOf(
                ShoppingDetailProduct(
                    1, "Leche entera", 2, "litros", "🥛"
                ),
                ShoppingDetailProduct(
                    2, "Pan integral", 1, "pieza", "🍞"
                ),
                ShoppingDetailProduct(
                    3, "Huevos", 12, "pzas", "🥚"
                ),
                ShoppingDetailProduct(
                    4, "Queso manchego", 200, "g", "🧀"
                ),
                ShoppingDetailProduct(
                    5, "Manzanas rojas", 6, "pzas", "🍎", true
                ),
                ShoppingDetailProduct(
                    6, "Tomates", 500, "g", "🍅", true
                )
            )
        )
    )

    val uiState: StateFlow<ShoppingDetailUiState> =
        _uiState.asStateFlow()

    // Cambiar el estado de comprado o pendiente
    fun togglePurchased(productId: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                products = currentState.products.map { product ->
                    if (product.id == productId) {
                        product.copy(purchased = !product.purchased)
                    } else {
                        product
                    }
                }
            )
        }
    }

    // Modificar la cantidad de un producto
    fun updateQuantity(productId: Int, newQuantity: Int) {
        if (newQuantity < 1) return

        _uiState.update { currentState ->
            currentState.copy(
                products = currentState.products.map { product ->
                    if (product.id == productId) {
                        product.copy(quantity = newQuantity)
                    } else {
                        product
                    }
                }
            )
        }
    }

    // Eliminar un producto de la lista
    fun deleteProduct(productId: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                products = currentState.products.filterNot {
                    it.id == productId
                }
            )
        }
    }

    // Cargar temporalmente los datos de una lista
    fun loadList(listId: Int) {
        _uiState.update { currentState ->
            currentState.copy(listId = listId)
        }
    }
}