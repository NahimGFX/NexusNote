package com.example.nexusnote.ui.addProduct

import androidx.lifecycle.ViewModel
import com.example.nexusnote.data.repository.ProductRepository
import com.example.nexusnote.data.repository.ShoppingListItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import com.example.nexusnote.data.local.entity.ProductEntity
import com.example.nexusnote.data.local.entity.ShoppingListItemEntity
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class AddProductUiState(
    val nombre: String = "",
    val cantidad: Int = 1,
    val unidad: String = "pzas",
    val imagenSeleccionada: String = "Sin foto",
    val errorNombre: String? = null,
    val mensaje: String? = null
) {
    val formularioValido: Boolean
        get() = nombre.isNotBlank() && cantidad > 0
}

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val shoppingListItemRepository: ShoppingListItemRepository
) : ViewModel() {


    private val _uiState = MutableStateFlow(AddProductUiState())

    val uiState: StateFlow<AddProductUiState> =
        _uiState.asStateFlow()

    fun actualizarNombre(nombre: String) {
        _uiState.update {
            it.copy(
                nombre = nombre,
                errorNombre = null,
                mensaje = null
            )
        }
    }

    fun aumentarCantidad() {
        _uiState.update {
            if (it.cantidad < 9999) {
                it.copy(cantidad = it.cantidad + 1)
            } else {
                it
            }
        }
    }

    fun disminuirCantidad() {
        _uiState.update {
            if (it.cantidad > 1) {
                it.copy(cantidad = it.cantidad - 1)
            } else {
                it
            }
        }
    }

    fun seleccionarUnidad(unidad: String) {
        val unidadesValidas = listOf(
            "pzas", "kg", "g", "litros",
            "ml", "paquetes", "latas"
        )

        if (unidad in unidadesValidas) {
            _uiState.update {
                it.copy(unidad = unidad)
            }
        }
    }

    fun seleccionarImagen(imagen: String) {
        _uiState.update {
            it.copy(imagenSeleccionada = imagen)
        }
    }

    fun validarFormulario(): Boolean {
        val estado = _uiState.value

        if (estado.nombre.isBlank()) {
            _uiState.update {
                it.copy(
                    errorNombre = "Ingrese el nombre del producto"
                )
            }
            return false
        }

        if (estado.cantidad <= 0) {
            _uiState.update {
                it.copy(
                    mensaje = "La cantidad debe ser mayor a cero"
                )
            }
            return false
        }

        return true
    }


    fun guardarProducto(
        listId: Int,
        onSuccess: () -> Unit
    ) {
        if (!validarFormulario()) return

        if (listId <= 0) {
            _uiState.update {
                it.copy(mensaje = "La lista de compras no es válida")
            }
            return
        }

        val estado = _uiState.value

        viewModelScope.launch {
            try {
                val productId = productRepository.insertProduct(
                    ProductEntity(
                        name = estado.nombre.trim()
                    )
                )

                shoppingListItemRepository.insertItem(
                    ShoppingListItemEntity(
                        shoppingListId = listId,
                        productId = productId,
                        quantity = estado.cantidad
                    )
                )

                limpiarFormulario()
                onSuccess()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        mensaje = "No se pudo guardar el producto"
                    )
                }
            }
        }
    }

    fun limpiarFormulario() {
        _uiState.value = AddProductUiState()
    }
}
