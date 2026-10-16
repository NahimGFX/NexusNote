package com.example.nexusnote.data.local.model

data class ShoppingListSummary(
    val id: Int,
    val name: String,
    val createdAt: Long,
    val productCount: Int,
    val purchasedCount: Int
)