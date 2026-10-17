
package com.example.nexusnote.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShoppingDetailScreen(
    listId: Int,
    onBackClick: () -> Unit,
    onAddProductClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Shopping list details")
        Text("List ID: $listId")

        Button(onClick = onAddProductClick) {
            Text("Add product")
        }

        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}
