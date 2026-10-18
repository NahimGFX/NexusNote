
package com.example.nexusnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nexusnote.data.repository.ShoppingListRepository

class CreateListViewModelFactory(
    private val repository: ShoppingListRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(CreateListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateListViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
