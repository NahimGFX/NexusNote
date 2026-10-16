
package com.example.nexusnote.data.repository

import com.example.nexusnote.data.local.dao.ShoppingListDao
import com.example.nexusnote.data.local.entity.ShoppingListEntity
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {
    fun getAllLists(): Flow<List<ShoppingListEntity>>
    fun getListById(listId: Int): Flow<ShoppingListEntity?>
    suspend fun insertList(name: String): Int
    suspend fun updateList(list: ShoppingListEntity)
    suspend fun deleteList(list: ShoppingListEntity)
}

class OfflineShoppingListRepository(
    private val shoppingListDao: ShoppingListDao
) : ShoppingListRepository {

    override fun getAllLists(): Flow<List<ShoppingListEntity>> =
        shoppingListDao.getAllLists()

    override fun getListById(listId: Int): Flow<ShoppingListEntity?> =
        shoppingListDao.getListById(listId)

    override suspend fun insertList(name: String): Int =
        shoppingListDao.insertList(
            ShoppingListEntity(name = name)
        ).toInt()

    override suspend fun updateList(list: ShoppingListEntity) {
        shoppingListDao.updateList(list)
    }

    override suspend fun deleteList(list: ShoppingListEntity) {
        shoppingListDao.deleteList(list)
    }
}
