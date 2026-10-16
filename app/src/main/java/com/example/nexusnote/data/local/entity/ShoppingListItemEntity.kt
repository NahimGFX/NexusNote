package com.example.nexusnote.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_list_items",
    foreignKeys = [
        ForeignKey(
            entity = ShoppingListEntity::class,
            parentColumns = ["id"],
            childColumns = ["shoppingListId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("shoppingListId"),
        Index("productId")
    ]
)
data class ShoppingListItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val shoppingListId: Int,
    val productId: Int,
    val quantity: Int = 1,
    val isPurchased: Boolean = false
)
