
package com.example.nexusnote.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.nexusnote.data.local.entity.ShoppingListEntity
import kotlinx.coroutines.flow.Flow
import com.example.nexusnote.data.local.model.ShoppingListSummary
@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shopping_lists ORDER BY createdAt DESC")
    fun getAllLists(): Flow<List<ShoppingListEntity>>

    @Query("SELECT * FROM shopping_lists WHERE id = :listId")
    fun getListById(listId: Int): Flow<ShoppingListEntity?>

    @Insert
    suspend fun insertList(list: ShoppingListEntity): Long

    @Update
    suspend fun updateList(list: ShoppingListEntity)

    @Delete
    suspend fun deleteList(list: ShoppingListEntity)

    @Query("""
    SELECT
        shopping_lists.id AS id,
        shopping_lists.name AS name,
        shopping_lists.createdAt AS createdAt,
        COUNT(shopping_list_items.id) AS productCount,
        COALESCE(
            SUM(CASE WHEN shopping_list_items.isPurchased = 1 THEN 1 ELSE 0 END),
            0
        ) AS purchasedCount
    FROM shopping_lists
    LEFT JOIN shopping_list_items
        ON shopping_list_items.shoppingListId = shopping_lists.id
    GROUP BY
        shopping_lists.id,
        shopping_lists.name,
        shopping_lists.createdAt
    ORDER BY shopping_lists.createdAt DESC
""")
    fun getListSummaries(): Flow<List<ShoppingListSummary>>

    @Query("DELETE FROM shopping_lists WHERE id = :listId")
    suspend fun deleteListById(listId: Int)
}
