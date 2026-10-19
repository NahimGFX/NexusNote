package com.example.nexusnote.di

import com.example.nexusnote.data.repository.OfflineProductRepository
import com.example.nexusnote.data.repository.OfflineShoppingListItemRepository
import com.example.nexusnote.data.repository.OfflineShoppingListRepository
import com.example.nexusnote.data.repository.ProductRepository
import com.example.nexusnote.data.repository.ShoppingListItemRepository
import com.example.nexusnote.data.repository.ShoppingListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        implementation: OfflineProductRepository
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindShoppingListRepository(
        implementation: OfflineShoppingListRepository
    ): ShoppingListRepository

    @Binds
    @Singleton
    abstract fun bindShoppingListItemRepository(
        implementation: OfflineShoppingListItemRepository
    ): ShoppingListItemRepository
}
