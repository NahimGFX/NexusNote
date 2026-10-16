
package com.example.nexusnote.data.repository

import com.example.nexusnote.data.local.dao.ShoppingListItemDao
import com.example.nexusnote.data.local.entity.ShoppingListItemEntity
import kotlinx.coroutines.flow.Flow

interface ShoppingListItemRepository {
    fun getItemsByList(listId: Int): Flow<List<ShoppingListItemEntity>>
    suspend fun insertItem(item: ShoppingListItemEntity): Int
    suspend fun updateItem(item: ShoppingListItemEntity)
    suspend fun deleteItem(itemId: Int)
}

class OfflineShoppingListItemRepository(
    private val itemDao: ShoppingListItemDao
) : ShoppingListItemRepository {

    override fun getItemsByList(
        listId: Int
    ): Flow<List<ShoppingListItemEntity>> =
        itemDao.getItemsByList(listId)

    override suspend fun insertItem(
        item: ShoppingListItemEntity
    ): Int = itemDao.insertItem(item).toInt()

    override suspend fun updateItem(item: ShoppingListItemEntity) {
        itemDao.updateItem(item)
    }

    override suspend fun deleteItem(itemId: Int) {
        itemDao.deleteItem(itemId)
    }
}
