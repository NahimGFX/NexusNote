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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): NexusNoteDatabase {
        return Room.databaseBuilder(
            context,
            NexusNoteDatabase::class.java,
            "nexusnote_database"
        ).build()
    }

    @Provides
    fun provideProductRepository(
        database: NexusNoteDatabase
    ): ProductRepository {
        return OfflineProductRepository(database.productDao())
    }

    @Provides
    fun provideShoppingListItemRepository(
        database: NexusNoteDatabase
    ): ShoppingListItemRepository {
        return OfflineShoppingListItemRepository(
            database.shoppingListItemDao()
        )
    }

    @Provides
    fun provideShoppingListRepository(
        database: NexusNoteDatabase
    ): ShoppingListRepository {
        return OfflineShoppingListRepository(
            database.shoppingListDao()
        )
    }
}