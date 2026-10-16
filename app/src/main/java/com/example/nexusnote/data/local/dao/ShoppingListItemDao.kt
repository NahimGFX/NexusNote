package com.example.nexusnote.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.nexusnote.data.local.entity.ShoppingListItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListItemDao {

    @Query("""
        SELECT * FROM shopping_list_items
        WHERE shoppingListId = :listId
        ORDER BY id DESC
    """)
    fun getItemsByList(listId: Int): Flow<List<ShoppingListItemEntity>>

    @Insert
    suspend fun insertItem(item: ShoppingListItemEntity): Long

    @Update
    suspend fun updateItem(item: ShoppingListItemEntity)

    @Query("DELETE FROM shopping_list_items WHERE id = :itemId")
    suspend fun deleteItem(itemId: Int)
}
