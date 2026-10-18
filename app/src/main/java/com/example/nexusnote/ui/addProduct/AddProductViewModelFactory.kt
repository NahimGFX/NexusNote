package com.example.nexusnote.ui.addProduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nexusnote.data.repository.ProductRepository
import com.example.nexusnote.data.repository.ShoppingListItemRepository

class AddProductViewModelFactory(
    private val productRepository: ProductRepository,
    private val shoppingListItemRepository: ShoppingListItemRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddProductViewModel(
                productRepository,
                shoppingListItemRepository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
