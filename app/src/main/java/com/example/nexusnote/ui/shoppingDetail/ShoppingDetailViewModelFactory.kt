package com.example.nexusnote.ui.shoppingDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.nexusnote.data.repository.ProductRepository
import com.example.nexusnote.data.repository.ShoppingListItemRepository
import com.example.nexusnote.data.repository.ShoppingListRepository

class ShoppingDetailViewModelFactory(
    private val itemRepository: ShoppingListItemRepository,
    private val productRepository: ProductRepository,
    private val listRepository: ShoppingListRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(
                ShoppingDetailViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingDetailViewModel(
                itemRepository = itemRepository,
                productRepository = productRepository,
                listRepository = listRepository
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}