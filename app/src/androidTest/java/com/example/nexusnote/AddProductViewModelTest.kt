package com.example.nexusnote

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nexusnote.data.local.NexusNoteDatabase
import com.example.nexusnote.data.repository.OfflineProductRepository
import com.example.nexusnote.data.repository.OfflineShoppingListItemRepository
import com.example.nexusnote.data.repository.OfflineShoppingListRepository
import com.example.nexusnote.ui.addProduct.AddProductEvent
import com.example.nexusnote.ui.addProduct.AddProductViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddProductViewModelTest {

    private lateinit var database: NexusNoteDatabase
    private lateinit var viewModel: AddProductViewModel
    private lateinit var listRepository: OfflineShoppingListRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            NexusNoteDatabase::class.java
        ).build()

        listRepository = OfflineShoppingListRepository(database.shoppingListDao())
        viewModel = AddProductViewModel(
            productRepository = OfflineProductRepository(database.productDao()),
            shoppingListItemRepository = OfflineShoppingListItemRepository(
                database.shoppingListItemDao()
            )
        )
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun listaInvalidaEmiteMensajeComoEvento() = runBlocking {
        viewModel.actualizarNombre("Leche")

        viewModel.guardarProducto(listId = 0)

        assertEquals(
            AddProductEvent.ShowMessage("La lista de compras no es válida"),
            withTimeout(2_000) { viewModel.events.first() }
        )
    }

    @Test
    fun guardadoEmiteNavegacionUnaVezYPersisteElProducto() = runBlocking {
        val listId = listRepository.insertList("Compras")
        viewModel.actualizarNombre("Leche")
        val nextEvent = async { viewModel.events.first() }

        viewModel.guardarProducto(listId)

        assertEquals(
            AddProductEvent.NavigateBack,
            withTimeout(2_000) { nextEvent.await() }
        )
        assertEquals("Leche", database.productDao().getAllProducts().first().single().name)
        assertEquals(1, database.shoppingListItemDao().getItemsByList(listId).first().size)
        assertTrue(viewModel.uiState.value.nombre.isEmpty())
    }
}
