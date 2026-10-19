package com.example.nexusnote.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nexusnote.R

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onViewListsClick: () -> Unit,
    onCreateListClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.home_welcome),
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = stringResource(R.string.home_description),
            style = MaterialTheme.typography.bodyLarge
        )

        when {
            uiState.isLoading -> {
                CircularProgressIndicator()

                Text(
                    text = stringResource(R.string.home_loading)
                )
            }

            uiState.hasError -> {
                Text(
                    text = stringResource(R.string.home_error),
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.home_summary),
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = stringResource(
                                R.string.home_total_lists,
                                uiState.totalLists
                            )
                        )

                        Text(
                            text = stringResource(
                                R.string.home_total_items,
                                uiState.totalItems
                            )
                        )

                        Text(
                            text = stringResource(
                                R.string.home_purchased_items,
                                uiState.purchasedItems
                            )
                        )
                    }
                }

                if (uiState.totalLists == 0) {
                    Text(
                        text = stringResource(R.string.home_empty)
                    )
                }
            }
        }

        Button(
            onClick = onViewListsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.home_view_lists))
        }

        OutlinedButton(
            onClick = onCreateListClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.home_create_list))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            uiState = HomeUiState(
                totalLists = 3,
                totalItems = 8,
                purchasedItems = 3,
                isLoading = false,
                //hasError = true
            ),
            onViewListsClick = {},
            onCreateListClick = {}
        )
    }
}
