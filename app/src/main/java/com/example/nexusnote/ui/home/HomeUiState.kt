package com.example.nexusnote.ui.home

data class HomeUiState(
    val totalLists: Int = 0,
    val totalItems: Int = 0,
    val purchasedItems: Int = 0,
    val isLoading: Boolean = true,
    val hasError: Boolean = false
)