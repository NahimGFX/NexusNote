
package com.example.nexusnote.di

import android.content.Context
import androidx.room.Room
import com.example.nexusnote.data.local.NexusNoteDatabase
import com.example.nexusnote.data.repository.OfflineProductRepository
import com.example.nexusnote.data.repository.OfflineShoppingListItemRepository
import com.example.nexusnote.data.repository.OfflineShoppingListRepository
import com.example.nexusnote.data.repository.ProductRepository
import com.example.nexusnote.data.repository.ShoppingListItemRepository
import com.example.nexusnote.data.repository.ShoppingListRepository

class AppContainer(context: Context) {

    val database: NexusNoteDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            NexusNoteDatabase::class.java,
            "nexusnote_database"
        ).build()

    val shoppingListRepository: ShoppingListRepository =
        OfflineShoppingListRepository(database.shoppingListDao())

    val productRepository: ProductRepository =
        OfflineProductRepository(database.productDao())

    val shoppingListItemRepository: ShoppingListItemRepository =
        OfflineShoppingListItemRepository(database.shoppingListItemDao())
}
