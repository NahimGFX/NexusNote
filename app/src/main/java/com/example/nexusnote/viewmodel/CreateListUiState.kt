
package com.example.nexusnote.viewmodel

data class CreateListUiState(
    val name: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)
