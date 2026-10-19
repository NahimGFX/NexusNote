package com.example.nexusnote.viewmodel

data class ShoppingListUi(
    val id: Int,
    val name: String,
    val dateLabel: String,
    val purchasedCount: Int,
    val productCount: Int,

    val productPreviewEmojis: List<String> = emptyList(),
    val icon: String? = null
) {
    val progressPercent: Int
        get() = if (productCount <= 0) 0
        else (purchasedCount.coerceIn(0, productCount) * 100) / productCount
}
sealed interface ListsUiState {
    data object Loading : ListsUiState
    data class Success(val lists: List<ShoppingListUi>) : ListsUiState
    data class Error(val message: String) : ListsUiState
}
