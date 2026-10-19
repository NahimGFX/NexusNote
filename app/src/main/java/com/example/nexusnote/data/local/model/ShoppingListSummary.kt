package com.example.nexusnote.data.local.model

data class ShoppingListSummary(
    val id: Int,
    val name: String,
    val icon: String?,
    val createdAt: Long,
    val productCount: Int,
    val purchasedCount: Int
)