
package com.example.nexusnote.ui.screens.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nexusnote.viewmodel.CreateListUiState

@Composable
fun CreateListScreen(
    uiState: CreateListUiState,
    onNameChange: (String) -> Unit,
    onCreateClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Create shopping list")

        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            label = { Text("List name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isSaving
        )

        if (uiState.error != null) {
            Text(uiState.error)
        }

        Button(
            onClick = onCreateClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSaving
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator()
            } else {
                Text("Create")
            }
        }

        Button(
            onClick = onBackClick,
            enabled = !uiState.isSaving
        ) {
            Text("Back")
        }
    }
}
