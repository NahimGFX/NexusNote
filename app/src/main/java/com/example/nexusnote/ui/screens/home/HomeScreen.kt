
package com.example.nexusnote.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nexusnote.viewmodel.HomeUiState


@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onCreateListClick: () -> Unit,
    onListClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "NexusNote",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Your shopping lists",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        Button(
            onClick = onCreateListClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create list")
        }

        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(24.dp)
                )
            }

            uiState.error != null -> {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            uiState.lists.isEmpty() -> {
                Text(
                    text = "You don't have any shopping lists yet.",
                    modifier = Modifier.padding(top = 24.dp)
                )
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.lists,
                        key = { it.id }
                    ) { list ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onListClick(list.id)
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = list.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Open shopping list"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
