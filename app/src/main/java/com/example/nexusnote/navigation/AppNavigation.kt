package com.example.nexusnote.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nexusnote.ui.screens.home.HomeScreen
import com.example.nexusnote.ui.screens.lists.ListsScreen
import com.example.nexusnote.ui.screens.create.CreateListScreen
import com.example.nexusnote.ui.addProduct.AddProductScreen
import com.example.nexusnote.ui.addProduct.AddProductViewModel
import com.example.nexusnote.ui.shoppingDetail.ShoppingDetailScreen
import com.example.nexusnote.ui.shoppingDetail.ShoppingDetailViewModel
import com.example.nexusnote.viewmodel.HomeViewModel
import com.example.nexusnote.viewmodel.ListsViewModel
import com.example.nexusnote.viewmodel.CreateListViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LISTS
    ) {

        // PANTALLA DE INICIO
        composable(Routes.HOME) {

            val homeViewModel: HomeViewModel = hiltViewModel()

            val uiState by homeViewModel.uiState
                .collectAsStateWithLifecycle()

            HomeScreen(
                uiState = uiState,
                onCreateListClick = {
                    navController.navigate(Routes.CREATE_LIST)
                },
                onListClick = { listId ->
                    navController.navigate(
                        Routes.shoppingDetail(listId)
                    )
                }
            )
        }

        // PANTALLA DE LISTAS
        composable(Routes.LISTS) {

            val listsViewModel: ListsViewModel = hiltViewModel()

            val uiState by listsViewModel.uiState
                .collectAsStateWithLifecycle()

            ListsScreen(
                uiState = uiState,
                onOpenList = { listId ->
                    navController.navigate(
                        Routes.shoppingDetail(listId)
                    )
                },
                onDeleteList = listsViewModel::deleteList,
                onCreateClick = {
                    navController.navigate(Routes.CREATE_LIST)
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
                        popUpTo(Routes.HOME)
                    }
                }
            }

            CreateListScreen(
                uiState = uiState,
                onNameChange = createListViewModel::onNameChange,
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

            val listId = backStackEntry.arguments
                ?.getInt(Routes.LIST_ID) ?: 0

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
                    navController.navigate(
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

            val listId = backStackEntry.arguments
                ?.getInt(Routes.LIST_ID) ?: 0

            val addProductViewModel: AddProductViewModel =
                hiltViewModel(backStackEntry)
            val addProductUiState by addProductViewModel.uiState
                .collectAsStateWithLifecycle()

            AddProductScreen(
                uiState = addProductUiState,
                onBackClick = {
                    navController.popBackStack()
                },
                onNombreChange = addProductViewModel::actualizarNombre,
                onAumentarCantidad = addProductViewModel::aumentarCantidad,
                onDisminuirCantidad = addProductViewModel::disminuirCantidad,
                onUnidadChange = addProductViewModel::seleccionarUnidad,
                onImagenChange = addProductViewModel::seleccionarImagen,
                onAddProduct = {
                    addProductViewModel.guardarProducto(
                        listId = listId,
                        onSuccess = {
                            navController.popBackStack()
                        }
                    )
                }
            )
        }
    }
}
