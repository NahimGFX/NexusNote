package com.example.nexusnote.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nexusnote.data.local.dao.ProductDao
import com.example.nexusnote.data.local.dao.ShoppingListDao
import com.example.nexusnote.data.local.dao.ShoppingListItemDao
import com.example.nexusnote.data.local.NexusNoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE shopping_lists ADD COLUMN icon TEXT NOT NULL DEFAULT '🛒'"
        )
    }
}

// Preserve compatibility with databases created by the previous local build.
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE shopping_list_items ADD COLUMN purchasedQuantity INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE shopping_list_items ADD COLUMN unit TEXT NOT NULL DEFAULT 'unidades'")
        db.execSQL("UPDATE shopping_list_items SET purchasedQuantity = CASE WHEN isPurchased = 1 THEN quantity ELSE 0 END")
    }
}
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
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }

    @Provides
    fun provideShoppingListDao(
        database: NexusNoteDatabase
    ): ShoppingListDao = database.shoppingListDao()

    @Provides
    fun provideProductDao(
        database: NexusNoteDatabase
    ): ProductDao = database.productDao()

    @Provides
    fun provideShoppingListItemDao(
        database: NexusNoteDatabase
    ): ShoppingListItemDao = database.shoppingListItemDao()
}
