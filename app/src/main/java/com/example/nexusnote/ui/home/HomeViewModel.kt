package com.example.nexusnote.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexusnote.data.repository.ShoppingListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ShoppingListRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        repository.getListSummaries()
            .map { summaries ->
                HomeUiState(
                    totalLists = summaries.size,
                    totalItems = summaries.sumOf { it.productCount },
                    purchasedItems = summaries.sumOf { it.purchasedCount },
                    isLoading = false,
                    hasError = false
                )
            }
            .catch {
                emit(
                    HomeUiState(
                        isLoading = false,
                        hasError = true
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState()
            )
}
