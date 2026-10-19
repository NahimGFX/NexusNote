package com.example.nexusnote

import com.example.nexusnote.ui.shoppingDetail.ShoppingDetailProduct
import com.example.nexusnote.ui.shoppingDetail.ShoppingDetailUiState
import com.example.nexusnote.ui.home.HomeUiState
import org.junit.Assert.*
import org.junit.Test

class PurchaseProgressTest {
    @Test fun threeOfTenIsPartialWithSevenRemaining() {
        val product = ShoppingDetailProduct(1, "Leche", 10, purchasedQuantity = 3)
        val state = ShoppingDetailUiState(products = listOf(product))
        assertEquals(7, product.quantity - product.purchasedQuantity)
        assertEquals(30, state.percentage)
        assertEquals(1, state.pendingProducts.size)
        assertEquals(0, state.purchasedCount)
    }
    @Test fun differentUnitsHaveEqualProductWeight() {
        val state = ShoppingDetailUiState(products = listOf(
            ShoppingDetailProduct(1, "Arroz", 1000, unit = "g", purchased = true, purchasedQuantity = 1000),
            ShoppingDetailProduct(2, "Leche", 2, unit = "litros", purchasedQuantity = 0)
        ))
        assertEquals(50, state.percentage)
        assertEquals(0.5f, HomeUiState(totalItems = 2, completionSum = 1.0).progress, 0.0001f)
    }
    @Test fun emptyListHasZeroProgress() {
        assertEquals(0, ShoppingDetailUiState().percentage)
        assertEquals(0f, HomeUiState().progress, 0f)
    }
}
