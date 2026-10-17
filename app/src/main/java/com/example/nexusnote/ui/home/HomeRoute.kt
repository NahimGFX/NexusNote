package com.example.nexusnote.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nexusnote.data.repository.ShoppingListRepository

@Composable
fun HomeRoute(
    repository: ShoppingListRepository,
    onViewListsClick: () -> Unit,
    onCreateListClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val homeViewModel: HomeViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                HomeViewModel(repository)
            }
        }
    )

    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onViewListsClick = onViewListsClick,
        onCreateListClick = onCreateListClick,
        modifier = modifier
    )
}