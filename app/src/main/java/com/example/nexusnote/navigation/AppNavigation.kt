package com.example.nexusnote.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nexusnote.ui.screens.home.HomeScreen
import com.example.nexusnote.ui.screens.create.CreateListScreen
import com.example.nexusnote.ui.screens.detail.ShoppingDetailScreen
import com.example.nexusnote.ui.addProduct.AddProductScreen
import com.example.nexusnote.ui.addProduct.AddProductViewModel
import com.example.nexusnote.ui.addProduct.AddProductViewModelFactory
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nexusnote.NexusNoteApplication
import com.example.nexusnote.viewmodel.HomeViewModel
import com.example.nexusnote.viewmodel.HomeViewModelFactory
import androidx.compose.runtime.LaunchedEffect
import com.example.nexusnote.viewmodel.CreateListViewModel
import com.example.nexusnote.viewmodel.CreateListViewModelFactory
import com.example.nexusnote.ui.screens.lists.ListsScreen
import com.example.nexusnote.viewmodel.ListsViewModel
import com.example.nexusnote.viewmodel.ListsViewModelFactory
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LISTS
    ) {

        composable(Routes.HOME) {
            val context = LocalContext.current
            val application = context.applicationContext as NexusNoteApplication

            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(
                    application.container.shoppingListRepository
                )
            )

            val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

            HomeScreen(
                uiState = uiState,
                onCreateListClick = {
                    navController.navigate(Routes.CREATE_LIST)
                },
                onListClick = { listId ->
                    navController.navigate(Routes.shoppingDetail(listId))
                }
            )
        }
        composable(Routes.LISTS) {
            val context = LocalContext.current
            val application = context.applicationContext as NexusNoteApplication
            val listsViewModel: ListsViewModel = viewModel(
                factory = ListsViewModelFactory(application.container.shoppingListRepository)
            )
            val uiState by listsViewModel.uiState.collectAsStateWithLifecycle()

            ListsScreen(
                uiState = uiState,
                onOpenList = { listId -> navController.navigate(Routes.shoppingDetail(listId)) },
                onDeleteList = listsViewModel::deleteList,
                onCreateClick = { navController.navigate(Routes.CREATE_LIST) },
                onRetryClick = listsViewModel::retry
            )
        }


        composable(Routes.CREATE_LIST) {
            val context = LocalContext.current
            val application = context.applicationContext as NexusNoteApplication

            val createListViewModel: CreateListViewModel = viewModel(
                factory = CreateListViewModelFactory(
                    application.container.shoppingListRepository
                )
            )

            val uiState by createListViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(createListViewModel) {
                createListViewModel.events.collect { listId ->
                    navController.navigate(Routes.shoppingDetail(listId)) {
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


        composable(
            route = Routes.SHOPPING_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(Routes.LIST_ID) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val listId =
                backStackEntry.arguments?.getInt(Routes.LIST_ID) ?: 0

            ShoppingDetailScreen(
                listId = listId,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddProductClick = {
                    navController.navigate(Routes.addProduct(listId))
                }
            )
        }

        composable(
            route = Routes.ADD_PRODUCT_ROUTE,
            arguments = listOf(
                navArgument(Routes.LIST_ID) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val listId =
                backStackEntry.arguments?.getInt(Routes.LIST_ID) ?: 0

            val addProductViewModel: AddProductViewModel = hiltViewModel()

            AddProductScreen(
                viewModel = addProductViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddProduct = { nombre, cantidad, unidad ->
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
