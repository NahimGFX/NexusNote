package com.example.nexusnote.navigation

object Routes {
    const val HOME = "home"

    const val LISTS = HOME // Alias compatible con la pantalla principal de listas.
    const val CREATE_LIST = "create_list"
    const val SHOPPING_DETAIL = "shopping_detail"
    const val ADD_PRODUCT = "add_product"

    const val LIST_ID = "listId"

    const val SHOPPING_DETAIL_ROUTE = "$SHOPPING_DETAIL/{$LIST_ID}"
    const val ADD_PRODUCT_ROUTE = "$ADD_PRODUCT/{$LIST_ID}"

    fun shoppingDetail(listId: Int): String {
        return "$SHOPPING_DETAIL/$listId"
    }

    fun addProduct(listId: Int): String {
        return "$ADD_PRODUCT/$listId"
    }
}