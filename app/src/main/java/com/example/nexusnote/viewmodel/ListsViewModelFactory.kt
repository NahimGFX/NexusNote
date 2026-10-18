package com.example.nexusnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nexusnote.data.repository.ShoppingListRepository

class ListsViewModelFactory(
    private val repository: ShoppingListRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
