package com.example.nexusnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexusnote.data.repository.ShoppingListRepository
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ListsViewModel(
    private val repository: ShoppingListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListsUiState>(ListsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var observeJob: Job? = null

    init {
        observeLists()
    }

    fun retry() {
        observeLists()
    }

    fun deleteList(listId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteListById(listId)
                // La consulta Flow de Room emitirá automáticamente la lista actualizada.
            } catch (exception: Exception) {
                _uiState.value = ListsUiState.Error(
                    exception.message ?: "No se pudo eliminar la lista."
                )
            }
        }
    }

    private fun observeLists() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            repository.getListSummaries()
                .onStart { _uiState.value = ListsUiState.Loading }
                .catch { exception ->
                    _uiState.value = ListsUiState.Error(
                        exception.message ?: "No se pudieron cargar las listas."
                    )
                }
                .collect { summaries ->
                    _uiState.value = ListsUiState.Success(
                        summaries.map { summary ->
                            ShoppingListUi(
                                id = summary.id,
                                name = summary.name,
                                dateLabel = formatDate(summary.createdAt),
                                purchasedCount = summary.purchasedCount,
                                productCount = summary.productCount
                            )
                        }
                    )
                }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val listDay = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val daysAgo = ((today.timeInMillis - listDay.timeInMillis) / MILLIS_PER_DAY).toInt()

        return when (daysAgo) {
            0 -> "Hoy"
            1 -> "Ayer"
            in 2..6 -> "Hace $daysAgo días"
            else -> DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
                .format(Date(timestamp))
        }
    }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
    }
}
