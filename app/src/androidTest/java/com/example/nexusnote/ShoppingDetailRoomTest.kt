package com.example.nexusnote

import androidx.test.core.app.ApplicationProvider
import androidx.room.Room
import com.example.nexusnote.data.local.NexusNoteDatabase
import com.example.nexusnote.data.local.entity.ProductEntity
import com.example.nexusnote.data.local.entity.ShoppingListEntity
import com.example.nexusnote.data.local.entity.ShoppingListItemEntity
import com.example.nexusnote.data.repository.OfflineProductRepository
import com.example.nexusnote.data.repository.OfflineShoppingListItemRepository
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShoppingDetailRoomTest {

    private lateinit var database: NexusNoteDatabase
    private lateinit var productRepository: OfflineProductRepository
    private lateinit var itemRepository: OfflineShoppingListItemRepository

    private var listId = 0
    private var productId = 0

    @Before
    fun setup() = runBlocking {
        val context = ApplicationProvider
            .getApplicationContext<android.content.Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            NexusNoteDatabase::class.java
        ).build()

        productRepository = OfflineProductRepository(
            database.productDao()
        )

        itemRepository = OfflineShoppingListItemRepository(
            database.shoppingListItemDao()
        )

        listId = database.shoppingListDao().insertList(
            ShoppingListEntity(name = "Compras de prueba")
        ).toInt()

        productId = productRepository.insertProduct(
            ProductEntity(name = "Leche entera")
        )
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun insertarProductoEnLista() = runBlocking {
        val itemId = itemRepository.insertItem(
            ShoppingListItemEntity(
                shoppingListId = listId,
                productId = productId,
                quantity = 2
            )
        )

        val items = itemRepository.getItemsByList(listId).first()

        Assert.assertEquals(1, items.size)
        Assert.assertEquals(itemId, items.first().id)
        Assert.assertEquals(2, items.first().quantity)
        Assert.assertFalse(items.first().isPurchased)
    }

    @Test
    fun marcarProductoComoComprado() = runBlocking {
        val itemId = itemRepository.insertItem(
            ShoppingListItemEntity(
                shoppingListId = listId,
                productId = productId,
                quantity = 2
            )
        )

        val item = itemRepository
            .getItemsByList(listId)
            .first()
            .first { it.id == itemId }

        itemRepository.updateItem(
            item.copy(isPurchased = true)
        )

        val updatedItem = itemRepository
            .getItemsByList(listId)
            .first()
            .first { it.id == itemId }

        Assert.assertTrue(updatedItem.isPurchased)
    }

    @Test
    fun actualizarCantidadDeProducto() = runBlocking {
        val itemId = itemRepository.insertItem(
            ShoppingListItemEntity(
                shoppingListId = listId,
                productId = productId,
                quantity = 1
            )
        )

        val item = itemRepository
            .getItemsByList(listId)
            .first()
            .first { it.id == itemId }

        itemRepository.updateItem(
            item.copy(quantity = 5)
        )

        val updatedItem = itemRepository
            .getItemsByList(listId)
            .first()
            .first { it.id == itemId }

        Assert.assertEquals(5, updatedItem.quantity)
    }

    @Test
    fun eliminarProductoDeLista() = runBlocking {
        val itemId = itemRepository.insertItem(
            ShoppingListItemEntity(
                shoppingListId = listId,
                productId = productId
            )
        )

        itemRepository.deleteItem(itemId)

        val items = itemRepository.getItemsByList(listId).first()

        Assert.assertTrue(items.isEmpty())
    }

    @Test
    fun mostrarSoloProductosDeListaSeleccionada() = runBlocking {
        val otherListId = database.shoppingListDao().insertList(
            ShoppingListEntity(name = "Otra lista")
        ).toInt()

        itemRepository.insertItem(
            ShoppingListItemEntity(
                shoppingListId = listId,
                productId = productId
            )
        )

        val currentListItems =
            itemRepository.getItemsByList(listId).first()

        val otherListItems =
            itemRepository.getItemsByList(otherListId).first()

        Assert.assertEquals(1, currentListItems.size)
        Assert.assertTrue(otherListItems.isEmpty())
    }
}