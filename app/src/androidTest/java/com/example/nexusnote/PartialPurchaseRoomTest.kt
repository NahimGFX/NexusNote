package com.example.nexusnote

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nexusnote.data.local.NexusNoteDatabase
import com.example.nexusnote.data.local.entity.*
import com.example.nexusnote.data.repository.OfflineShoppingListItemRepository
import com.example.nexusnote.di.MIGRATION_2_3
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PartialPurchaseRoomTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val dbName = "partial-purchase-test.db"
    private lateinit var db: NexusNoteDatabase
    private lateinit var repo: OfflineShoppingListItemRepository
    private var listId = 0
    private var itemId = 0

    private fun openDb() {
        db = Room.databaseBuilder(context, NexusNoteDatabase::class.java, dbName)
            .addMigrations(MIGRATION_2_3).build()
        repo = OfflineShoppingListItemRepository(db.shoppingListItemDao())
    }
    @Before fun setup() = runBlocking {
        context.deleteDatabase(dbName)
        openDb()
        listId = db.shoppingListDao().insertList(ShoppingListEntity(name = "Prueba parcial")).toInt()
        val product = db.productDao().insertProduct(ProductEntity(name = "Leche")).toInt()
        itemId = repo.insertItem(ShoppingListItemEntity(shoppingListId = listId, productId = product, quantity = 10, unit = "litros"))
    }
    @After fun cleanup() {
        db.close()
        context.deleteDatabase(dbName)
    }
    @Test fun partialPurchaseSurvivesReopeningAndUpdatesSummary() = runBlocking {
        repo.setPurchasedQuantity(listId, itemId, 3)
        db.close()
        openDb()
        val item = repo.getItemsWithProductsByList(listId).first().single()
        assertEquals(3, item.purchasedQuantity)
        assertEquals(10, item.quantity)
        assertEquals("litros", item.unit)
        assertFalse(item.isPurchased)
        val summary = db.shoppingListDao().getListSummaries().first().single()
        assertEquals(0, summary.purchasedCount)
        assertEquals(3L, summary.purchasedUnits)
        assertEquals(0.3, summary.completionSum, 0.0001)
    }
    @Test fun directQuantityEditPreservesPurchaseAndRejectsSmallerTotal() = runBlocking {
        repo.setPurchasedQuantity(listId, itemId, 3)
        repo.setQuantity(listId, itemId, 100)
        try { repo.setQuantity(listId, itemId, 2); fail("Must reject quantity below purchased") }
        catch (_: IllegalStateException) { }
        val item = repo.getItemsByList(listId).first().single()
        assertEquals(100, item.quantity)
        assertEquals(3, item.purchasedQuantity)
    }
    @Test fun purchaseBoundsAreEnforcedAndCanBeCorrected() = runBlocking {
        repo.setPurchasedQuantity(listId, itemId, 10)
        assertTrue(repo.getItemsByList(listId).first().single().isPurchased)
        try { repo.setPurchasedQuantity(listId, itemId, 11); fail("Must reject overpurchase") }
        catch (_: IllegalStateException) { }
        try { repo.setPurchasedQuantity(listId, itemId, -1); fail("Must reject negative") }
        catch (_: IllegalArgumentException) { }
        repo.setPurchasedQuantity(listId, itemId, 0)
        assertFalse(repo.getItemsByList(listId).first().single().isPurchased)
        assertEquals(0, repo.getItemsByList(listId).first().single().purchasedQuantity)
    }
    @Test fun increasingCompletedQuantityMakesItemPending() = runBlocking {
        repo.setPurchasedQuantity(listId, itemId, 10)
        repo.setQuantity(listId, itemId, 15)
        val item = repo.getItemsByList(listId).first().single()
        assertEquals(10, item.purchasedQuantity)
        assertFalse(item.isPurchased)
    }
    @Test fun anotherListCannotModifyThisItem() = runBlocking {
        try { repo.setPurchasedQuantity(listId + 10, itemId, 3); fail("Wrong list must be rejected") }
        catch (_: IllegalStateException) { }
        assertEquals(0, repo.getItemsByList(listId).first().single().purchasedQuantity)
    }
    @Test fun migrationPreservesOldCompletedAndPendingPurchases() = runBlocking {
        db.close()
        context.deleteDatabase(dbName)
        val file = context.getDatabasePath(dbName)
        file.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(file, null).use { old ->
            old.execSQL("CREATE TABLE shopping_lists (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, icon TEXT NOT NULL DEFAULT '🛒', createdAt INTEGER NOT NULL)")
            old.execSQL("CREATE TABLE products (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, photoUri TEXT)")
            old.execSQL("CREATE TABLE shopping_list_items (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, shoppingListId INTEGER NOT NULL, productId INTEGER NOT NULL, quantity INTEGER NOT NULL, isPurchased INTEGER NOT NULL, FOREIGN KEY(shoppingListId) REFERENCES shopping_lists(id) ON UPDATE NO ACTION ON DELETE CASCADE, FOREIGN KEY(productId) REFERENCES products(id) ON UPDATE NO ACTION ON DELETE CASCADE)")
            old.execSQL("CREATE INDEX index_shopping_list_items_shoppingListId ON shopping_list_items(shoppingListId)")
            old.execSQL("CREATE INDEX index_shopping_list_items_productId ON shopping_list_items(productId)")
            old.execSQL("INSERT INTO shopping_lists VALUES(1, 'Anterior', '🛒', 0)")
            old.execSQL("INSERT INTO products VALUES(1, 'Producto anterior', NULL)")
            old.execSQL("INSERT INTO shopping_list_items VALUES(1, 1, 1, 10, 1)")
            old.execSQL("INSERT INTO shopping_list_items VALUES(2, 1, 1, 7, 0)")
            old.version = 2
        }
        openDb()
        val items = repo.getItemsByList(1).first().associateBy { it.id }
        assertEquals(10, items.getValue(1).purchasedQuantity)
        assertTrue(items.getValue(1).isPurchased)
        assertEquals(0, items.getValue(2).purchasedQuantity)
        assertEquals(7, items.getValue(2).quantity)
    }
}
