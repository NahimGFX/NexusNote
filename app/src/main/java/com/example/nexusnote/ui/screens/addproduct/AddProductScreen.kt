
package com.example.nexusnote.ui.screens.addproduct

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddProductScreen(
    listId: Int,
    onBackClick: () -> Unit,
    onProductSaved: () -> Unit
) {
    var productName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add product")
        Text("List ID: $listId")

        TextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Product name") }
        )

        Button(
            onClick = onProductSaved,
            enabled = productName.isNotBlank()
        ) {
            Text("Save product")
        }

        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}
