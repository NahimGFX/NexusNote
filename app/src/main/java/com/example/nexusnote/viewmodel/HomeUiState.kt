
package com.example.nexusnote.viewmodel

import com.example.nexusnote.data.local.entity.ShoppingListEntity

data class HomeUiState(
    val lists: List<ShoppingListEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
