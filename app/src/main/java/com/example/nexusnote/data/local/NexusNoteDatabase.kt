package com.example.nexusnote.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nexusnote.data.local.dao.ProductDao
import com.example.nexusnote.data.local.dao.ShoppingListDao
import com.example.nexusnote.data.local.dao.ShoppingListItemDao
import com.example.nexusnote.data.local.entity.ProductEntity
import com.example.nexusnote.data.local.entity.ShoppingListEntity
import com.example.nexusnote.data.local.entity.ShoppingListItemEntity

@Database(
    entities = [
        ShoppingListEntity::class,
        ProductEntity::class,
        ShoppingListItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class NexusNoteDatabase : RoomDatabase() {

    abstract fun shoppingListDao(): ShoppingListDao

    abstract fun productDao(): ProductDao

    abstract fun shoppingListItemDao(): ShoppingListItemDao
}
