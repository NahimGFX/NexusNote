package com.example.nexusnote.data.local.model

/** Projection for the reactive list-item and product join used by the detail screen. */
data class ShoppingListItemWithProduct(
    val itemId: Int,
    val quantity: Int,
    val isPurchased: Boolean,
    val productName: String?,
    val photoUri: String?
)
