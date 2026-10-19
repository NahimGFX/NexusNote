package com.example.nexusnote.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.material3.SnackbarHostState
import com.example.nexusnote.ui.screens.lists.ListsScreen
import com.example.nexusnote.ui.screens.create.CreateListScreen
import com.example.nexusnote.ui.addProduct.AddProductScreen
import com.example.nexusnote.ui.addProduct.AddProductViewModel
import com.example.nexusnote.ui.addProduct.AddProductEvent
import com.example.nexusnote.ui.shoppingDetail.ShoppingDetailScreen
import com.example.nexusnote.ui.shoppingDetail.ShoppingDetailViewModel
import com.example.nexusnote.viewmodel.ListsViewModel
import com.example.nexusnote.viewmodel.CreateListViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {

        // Pantalla principal: muestra y administra las listas de compras.
        composable(Routes.HOME) {
            val listsViewModel: ListsViewModel = hiltViewModel()

            val uiState by listsViewModel.uiState
                .collectAsStateWithLifecycle()

            ListsScreen(
                uiState = uiState,
                onOpenList = { listId ->
                    navController.navigateSingleTop(
                        Routes.shoppingDetail(listId)
                    )
                },
                onDeleteList = listsViewModel::deleteList,
                onCreateClick = {
                    navController.navigateSingleTop(Routes.CREATE_LIST)
                },
                onRetryClick = listsViewModel::retry
            )
        }

        // PANTALLA PARA CREAR LISTAS
        composable(Routes.CREATE_LIST) {

            val createListViewModel: CreateListViewModel = hiltViewModel()

            val uiState by createListViewModel.uiState
                .collectAsStateWithLifecycle()

            LaunchedEffect(createListViewModel) {
                createListViewModel.events.collect { listId ->
                    navController.navigate(
                        Routes.shoppingDetail(listId)
                    ) {
                        popUpTo(Routes.HOME) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            }

            CreateListScreen(
                uiState = uiState,
                onNameChange = createListViewModel::onNameChange,
                onIconChange = createListViewModel::onIconChange,
                onCreateClick = createListViewModel::createList,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // PANTALLA DETALLE DE COMPRAS DEL EQUIPO
        composable(
            route = Routes.SHOPPING_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(Routes.LIST_ID) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val listId = requireNotNull(
                backStackEntry.arguments
                    ?.takeIf { it.containsKey(Routes.LIST_ID) }
                    ?.getInt(Routes.LIST_ID)
            ) { "Falta el argumento obligatorio '${Routes.LIST_ID}'" }

            val detailViewModel: ShoppingDetailViewModel =
                hiltViewModel(backStackEntry)
            val detailUiState by detailViewModel.uiState
                .collectAsStateWithLifecycle()

            LaunchedEffect(detailViewModel, listId) {
                detailViewModel.loadList(listId)
            }

            ShoppingDetailScreen(
                uiState = detailUiState,
                onBack = {
                    navController.popBackStack()
                },
                onAddProduct = {
                    navController.navigateSingleTop(
                        Routes.addProduct(listId)
                    )
                },
                onToggleProduct = detailViewModel::togglePurchased,
                onQuantityChange = detailViewModel::updateQuantity,
                onDeleteProduct = detailViewModel::deleteProduct
            )
        }

        // PANTALLA AGREGAR PRODUCTO
        composable(
            route = Routes.ADD_PRODUCT_ROUTE,
            arguments = listOf(
                navArgument(Routes.LIST_ID) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val listId = requireNotNull(
                backStackEntry.arguments
                    ?.takeIf { it.containsKey(Routes.LIST_ID) }
                    ?.getInt(Routes.LIST_ID)
            ) { "Falta el argumento obligatorio '${Routes.LIST_ID}'" }

            val addProductViewModel: AddProductViewModel =
                hiltViewModel(backStackEntry)
            val addProductUiState by addProductViewModel.uiState
                .collectAsStateWithLifecycle()
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(addProductViewModel, snackbarHostState) {
                addProductViewModel.events.collect { event ->
                    when (event) {
                        is AddProductEvent.ShowMessage -> {
                            snackbarHostState.showSnackbar(event.message)
                        }
                        AddProductEvent.NavigateBack -> {
                            navController.popBackStack()
                        }
                    }
                }
            }

            AddProductScreen(
                uiState = addProductUiState,
                snackbarHostState = snackbarHostState,
                onBackClick = {
                    navController.popBackStack()
                },
                onNombreChange = addProductViewModel::actualizarNombre,
                onAumentarCantidad = addProductViewModel::aumentarCantidad,
                onDisminuirCantidad = addProductViewModel::disminuirCantidad,
                onUnidadChange = addProductViewModel::seleccionarUnidad,
                onImagenChange = addProductViewModel::seleccionarImagen,
                onPhotoUriChange = addProductViewModel::seleccionarFoto,
                onImageError = addProductViewModel::reportarErrorImagen,
                onAddProduct = { addProductViewModel.guardarProducto(listId) }
            )
        }
    }
}

private fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}
