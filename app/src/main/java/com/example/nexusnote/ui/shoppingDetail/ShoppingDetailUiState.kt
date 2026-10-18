package com.example.nexusnote.ui.shoppingDetail
data class ShoppingDetailProduct(
    val id: Int,
    val name: String,
    val quantity: Int,
    val unit: String = "unidades",
    val emoji: String = "🛒",
    val purchased: Boolean = false,
    val photoUri: String? = null
)

data class ShoppingDetailUiState(
    val listId: Int = 0,
    val listName: String = "Súper semanal",
    val dateLabel: String = "Hoy",
    val products: List<ShoppingDetailProduct> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val purchasedProducts: List<ShoppingDetailProduct>
        get() = products.filter { it.purchased }

    val pendingProducts: List<ShoppingDetailProduct>
        get() = products.filterNot { it.purchased }

    val purchasedCount: Int
        get() = purchasedProducts.size

    val totalCount: Int
        get() = products.size

    val progress: Float
        get() = if (totalCount == 0) {
            0f
        } else {
            purchasedCount.toFloat() / totalCount
        }

    val percentage: Int
        get() = (progress * 100).toInt()
}