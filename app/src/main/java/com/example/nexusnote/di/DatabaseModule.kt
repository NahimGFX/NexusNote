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
            .addMigrations(MIGRATION_1_2)
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
