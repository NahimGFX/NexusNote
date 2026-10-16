
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
import com.example.nexusnote.ui.screens.addproduct.AddProductScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onCreateListClick = {
                    navController.navigate(Routes.CREATE_LIST)
                },
                onListClick = { listId ->
                    navController.navigate(Routes.shoppingDetail(listId))
                }
            )
        }

        composable(Routes.CREATE_LIST) {
            CreateListScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onListCreated = { listId ->
                    navController.navigate(Routes.shoppingDetail(listId)) {
                        popUpTo(Routes.HOME)
                    }
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

            AddProductScreen(
                listId = listId,
                onBackClick = {
                    navController.popBackStack()
                },
                onProductSaved = {
                    navController.popBackStack()
                }
            )
        }
    }
}
