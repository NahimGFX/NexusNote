
package com.example.nexusnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexusnote.data.repository.ShoppingListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateListViewModel @Inject constructor(
    private val repository: ShoppingListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateListUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<Int>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onNameChange(name: String) {
        _uiState.update {
            it.copy(name = name, error = null)
        }
    }

    fun onIconChange(icon: String) {
        _uiState.update {
            it.copy(icon = icon)
        }
    }

    fun createList() {
        val name = _uiState.value.name.trim()
        val icon = _uiState.value.icon

        if (name.isBlank()) {
            _uiState.update {
                it.copy(error = "Enter a name for your list.")
            }
            return
        }

        if (_uiState.value.isSaving) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSaving = true, error = null)
            }

            try {
                val listId = repository.insertList(name, icon)
                _events.send(listId)

                _uiState.update {
                    it.copy(isSaving = false)
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = exception.message ?: "Could not save the list."
                    )
                }
            }
        }
    }
}
