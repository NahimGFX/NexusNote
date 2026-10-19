package com.example.nexusnote.data.repository

import com.example.nexusnote.data.local.dao.ShoppingListDao
import com.example.nexusnote.data.local.entity.ShoppingListEntity
import com.example.nexusnote.data.local.model.ShoppingListSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ShoppingListRepository {
    fun getAllLists(): Flow<List<ShoppingListEntity>>
    fun getListSummaries(): Flow<List<ShoppingListSummary>>
    fun getListById(listId: Int): Flow<ShoppingListEntity?>

    suspend fun insertList(name: String, icon: String = "🛒"): Int
    suspend fun updateList(list: ShoppingListEntity)
    suspend fun deleteList(list: ShoppingListEntity)
    suspend fun deleteListById(listId: Int)
}

class OfflineShoppingListRepository @Inject constructor(
    private val shoppingListDao: ShoppingListDao
) : ShoppingListRepository {

    override fun getAllLists(): Flow<List<ShoppingListEntity>> =
        shoppingListDao.getAllLists()

    override fun getListSummaries(): Flow<List<ShoppingListSummary>> =
        shoppingListDao.getListSummaries()

    override fun getListById(listId: Int): Flow<ShoppingListEntity?> =
        shoppingListDao.getListById(listId)

    override suspend fun insertList(name: String, icon: String): Int =
        shoppingListDao.insertList(
            ShoppingListEntity(name = name, icon = icon)
        ).toInt()

    override suspend fun updateList(list: ShoppingListEntity) {
        shoppingListDao.updateList(list)
    }

    override suspend fun deleteList(list: ShoppingListEntity) {
        shoppingListDao.deleteList(list)
    }

    override suspend fun deleteListById(listId: Int) {
        shoppingListDao.deleteListById(listId)
    }
}
