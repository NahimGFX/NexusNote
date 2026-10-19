package com.example.nexusnote

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nexusnote.data.local.NexusNoteDatabase
import com.example.nexusnote.data.local.entity.ProductEntity
import com.example.nexusnote.data.repository.ProductRepository
import com.example.nexusnote.data.repository.OfflineProductRepository
import com.example.nexusnote.data.repository.OfflineShoppingListItemRepository
import com.example.nexusnote.data.repository.OfflineShoppingListRepository
import com.example.nexusnote.ui.addProduct.AddProductEvent
import com.example.nexusnote.ui.addProduct.AddProductViewModel
import com.example.nexusnote.ui.addProduct.ProductImageReference
import com.example.nexusnote.ui.shoppingDetail.ShoppingDetailViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicInteger

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
        assertTrue(!viewModel.uiState.value.isSaving)
    }

    @Test
    fun referenciaDeImagenSePersisteYAlimentaElEstadoDelDetalle() = runBlocking {
        val listId = listRepository.insertList("Compras con foto")
        val imagePath = "/data/user/0/com.example.nexusnote/files/product_images/leche.jpg"
        val itemRepository = OfflineShoppingListItemRepository(
            database.shoppingListItemDao()
        )
        viewModel.actualizarNombre("Leche")
        viewModel.seleccionarFoto(imagePath)
        val nextEvent = async { viewModel.events.first() }

        viewModel.guardarProducto(listId)

        assertEquals(
            AddProductEvent.NavigateBack,
            withTimeout(2_000) { nextEvent.await() }
        )
        assertEquals(
            imagePath,
            database.productDao().getAllProducts().first().single().photoUri
        )

        val detailViewModel = ShoppingDetailViewModel(
            itemRepository = itemRepository,
            listRepository = listRepository
        )
        detailViewModel.loadList(listId)
        val detailState = withTimeout(2_000) {
            detailViewModel.uiState.first { state ->
                state.products.any { product -> product.photoUri == imagePath }
            }
        }

        assertEquals(imagePath, detailState.products.single().photoUri)
    }

    @Test
    fun imagenPredeterminadaSePersisteYSeResuelveComoDrawable() = runBlocking {
        val listId = listRepository.insertList("Compras con imagen predeterminada")
        val defaultImage = ProductImageReference.forCategory("Carnes")!!
        val itemRepository = OfflineShoppingListItemRepository(
            database.shoppingListItemDao()
        )
        viewModel.actualizarNombre("Carne molida")
        viewModel.seleccionarImagen("Carnes")
        viewModel.seleccionarFoto(defaultImage.reference)
        val nextEvent = async { viewModel.events.first() }

        viewModel.guardarProducto(listId)

        assertEquals(
            AddProductEvent.NavigateBack,
            withTimeout(2_000) { nextEvent.await() }
        )
        val storedPhotoReference = database.productDao()
            .getAllProducts()
            .first()
            .single()
            .photoUri
        assertEquals(defaultImage.reference, storedPhotoReference)

        val detailViewModel = ShoppingDetailViewModel(
            itemRepository = itemRepository,
            listRepository = listRepository
        )
        detailViewModel.loadList(listId)
        val detailProduct = withTimeout(2_000) {
            detailViewModel.uiState.first { state ->
                state.products.any { product -> product.photoUri == defaultImage.reference }
            }.products.single()
        }

        assertEquals(defaultImage.reference, detailProduct.photoUri)
        assertEquals(
            R.drawable.carnes,
            ProductImageReference.drawableResourceId(detailProduct.photoUri)
        )
    }

    @Test
    fun llamadasSimultaneasSoloInicianUnGuardado() = runBlocking {
        val listId = listRepository.insertList("Compras")
        val delegate = OfflineProductRepository(database.productDao())
        val insertStarted = CompletableDeferred<Unit>()
        val continueInsert = CompletableDeferred<Unit>()
        val insertCalls = AtomicInteger()
        val gatedProductRepository = object : ProductRepository by delegate {
            override suspend fun insertProduct(product: ProductEntity): Int {
                insertCalls.incrementAndGet()
                insertStarted.complete(Unit)
                continueInsert.await()
                return delegate.insertProduct(product)
            }
        }
        val guardedViewModel = AddProductViewModel(
            productRepository = gatedProductRepository,
            shoppingListItemRepository = OfflineShoppingListItemRepository(
                database.shoppingListItemDao()
            )
        )
        guardedViewModel.actualizarNombre("Leche")
        val nextEvent = async { guardedViewModel.events.first() }

        guardedViewModel.guardarProducto(listId)
        withTimeout(2_000) { insertStarted.await() }
        assertTrue(guardedViewModel.uiState.value.isSaving)

        guardedViewModel.guardarProducto(listId)
        assertEquals(1, insertCalls.get())

        continueInsert.complete(Unit)
        assertEquals(
            AddProductEvent.NavigateBack,
            withTimeout(2_000) { nextEvent.await() }
        )
        assertTrue(!guardedViewModel.uiState.value.isSaving)
        assertEquals(1, database.productDao().getAllProducts().first().size)
    }

    @Test
    fun errorDeGuardadoRestableceEstadoDeCarga() = runBlocking {
        val listId = listRepository.insertList("Compras")
        val failingProductRepository = object : ProductRepository by
            OfflineProductRepository(database.productDao()) {
            override suspend fun insertProduct(product: ProductEntity): Int {
                throw IllegalStateException("Error de prueba")
            }
        }
        val failingViewModel = AddProductViewModel(
            productRepository = failingProductRepository,
            shoppingListItemRepository = OfflineShoppingListItemRepository(
                database.shoppingListItemDao()
            )
        )
        failingViewModel.actualizarNombre("Leche")
        val nextEvent = async { failingViewModel.events.first() }

        failingViewModel.guardarProducto(listId)

        assertEquals(
            AddProductEvent.ShowMessage("No se pudo guardar el producto"),
            withTimeout(2_000) { nextEvent.await() }
        )
        assertTrue(!failingViewModel.uiState.value.isSaving)
    }
}
