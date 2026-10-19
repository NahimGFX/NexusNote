package com.example.nexusnote

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nexusnote.data.local.NexusNoteDatabase
import com.example.nexusnote.data.local.entity.ProductEntity
import com.example.nexusnote.data.local.entity.ShoppingListItemEntity
import com.example.nexusnote.data.repository.OfflineProductRepository
import com.example.nexusnote.data.repository.OfflineShoppingListItemRepository
import com.example.nexusnote.data.repository.OfflineShoppingListRepository
import com.example.nexusnote.ui.shoppingDetail.ShoppingDetailViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShoppingDetailViewModelTest {

    private lateinit var database: NexusNoteDatabase
    private lateinit var viewModel: ShoppingDetailViewModel
    private lateinit var productRepository: OfflineProductRepository

    private var listId = 0
    private var itemId = 0
    private var productId = 0

    @Before
    fun setup() = runBlocking {
        val context =
            ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            NexusNoteDatabase::class.java
        ).build()

        val listRepository = OfflineShoppingListRepository(
            database.shoppingListDao()
        )

        productRepository = OfflineProductRepository(
            database.productDao()
        )

        val itemRepository = OfflineShoppingListItemRepository(
            database.shoppingListItemDao()
        )

        listId = listRepository.insertList("Compras de prueba")

        productId = productRepository.insertProduct(
            ProductEntity(name = "Leche entera")
        )

        itemId = itemRepository.insertItem(
            ShoppingListItemEntity(
                shoppingListId = listId,
                productId = productId,
                quantity = 2
            )
        )

        viewModel = ShoppingDetailViewModel(
            itemRepository = itemRepository,
            listRepository = listRepository
        )

        viewModel.loadList(listId)

        waitUntil {
            !viewModel.uiState.value.isLoading &&
                    viewModel.uiState.value.products.size == 1
        }
    }

    private suspend fun waitUntil(
        condition: () -> Boolean
    ) {
        repeat(100) {
            if (condition()) return
            delay(50)
        }
        throw AssertionError(
            "El estado del ViewModel no se actualizó a tiempo"
        )
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun cargarProductosYNombreDeLista() = runBlocking {
        val state = viewModel.uiState.value

        assertEquals("Leche entera", state.products.first().name)
        assertEquals(2, state.products.first().quantity)

        waitUntil {
            viewModel.uiState.value.listName == "Compras de prueba"
        }

        assertEquals(
            "Compras de prueba",
            viewModel.uiState.value.listName
        )
    }

    @Test
    fun fechaDelDetalleReflejaLaFechaGuardadaEnRoom() = runBlocking {
        val storedList = database.shoppingListDao()
            .getListById(listId)
            .first()!!
        database.shoppingListDao().updateList(
            storedList.copy(createdAt = 0L)
        )

        val expectedDate = DateFormat
            .getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
            .format(Date(0L))

        waitUntil {
            viewModel.uiState.value.dateLabel == expectedDate
        }

        assertEquals(expectedDate, viewModel.uiState.value.dateLabel)
    }

    @Test
    fun listaInexistenteNoMuestraUnaFechaFalsa() = runBlocking {
        viewModel.loadList(Int.MAX_VALUE)

        waitUntil {
            !viewModel.uiState.value.isLoading &&
                    viewModel.uiState.value.listName == "Lista no encontrada"
        }

        assertEquals("", viewModel.uiState.value.dateLabel)
    }

    @Test
    fun actualizarProductoEnRoomActualizaElDetalleReactivo() = runBlocking {
        productRepository.updateProduct(
            ProductEntity(
                id = productId,
                name = "Leche deslactosada",
                photoUri = "content://products/leche"
            )
        )

        waitUntil {
            val product = viewModel.uiState.value.products.firstOrNull()
            product?.name == "Leche deslactosada" &&
                    product.photoUri == "content://products/leche"
        }

        val product = viewModel.uiState.value.products.single()
        assertEquals("Leche deslactosada", product.name)
        assertEquals("content://products/leche", product.photoUri)
    }

    @Test
    fun marcarProductoActualizaProgreso() = runBlocking {
        viewModel.togglePurchased(itemId)

        waitUntil {
            viewModel.uiState.value.purchasedCount == 1
        }

        val state = viewModel.uiState.value

        assertTrue(state.products.first().purchased)
        assertEquals(1, state.purchasedCount)
        assertEquals(100, state.percentage)
    }

    @Test
    fun modificarCantidadActualizaEstado() = runBlocking {
        viewModel.updateQuantity(itemId, 5)

        waitUntil {
            viewModel.uiState.value.products.first().quantity == 5
        }

        assertEquals(
            5,
            viewModel.uiState.value.products.first().quantity
        )
    }

    @Test
    fun eliminarProductoActualizaLista() = runBlocking {
        viewModel.deleteProduct(itemId)

        waitUntil {
            viewModel.uiState.value.products.isEmpty()
        }

        assertEquals(
            0,
            viewModel.uiState.value.totalCount
        )
    }

    @Test
    fun cantidadNoPuedeSerMenorAUno() = runBlocking {
        viewModel.updateQuantity(itemId, 0)

        delay(200)

        assertEquals(
            2,
            viewModel.uiState.value.products.first().quantity
        )
    }
}
