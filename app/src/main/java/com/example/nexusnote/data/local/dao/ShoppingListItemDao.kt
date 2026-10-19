package com.example.nexusnote.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.nexusnote.data.local.entity.ShoppingListItemEntity
import com.example.nexusnote.data.local.model.ShoppingListItemWithProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListItemDao {

    @Query("""
        SELECT * FROM shopping_list_items
        WHERE shoppingListId = :listId
        ORDER BY id DESC
    """)
    fun getItemsByList(listId: Int): Flow<List<ShoppingListItemEntity>>

    @Query(
        """
        SELECT
            shopping_list_items.id AS itemId,
            shopping_list_items.quantity AS quantity,
            shopping_list_items.isPurchased AS isPurchased,
            products.name AS productName,
            products.photoUri AS photoUri
        FROM shopping_list_items
        LEFT JOIN products
            ON products.id = shopping_list_items.productId
        WHERE shopping_list_items.shoppingListId = :listId
        ORDER BY shopping_list_items.id DESC
        """
    )
    fun getItemsWithProductsByList(
        listId: Int
    ): Flow<List<ShoppingListItemWithProduct>>

    @Insert
    suspend fun insertItem(item: ShoppingListItemEntity): Long

    @Update
    suspend fun updateItem(item: ShoppingListItemEntity)

    @Query("DELETE FROM shopping_list_items WHERE id = :itemId")
    suspend fun deleteItem(itemId: Int)
}
