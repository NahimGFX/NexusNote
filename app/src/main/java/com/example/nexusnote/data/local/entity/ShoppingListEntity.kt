package com.example.nexusnote.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_lists")
data class ShoppingListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    @ColumnInfo(defaultValue = "'🛒'")
    val icon: String = "🛒",
    val createdAt: Long = System.currentTimeMillis()
)
