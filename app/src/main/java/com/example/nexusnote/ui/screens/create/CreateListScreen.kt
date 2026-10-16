
package com.example.nexusnote.ui.screens.create

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
fun CreateListScreen(
    onBackClick: () -> Unit,
    onListCreated: (Int) -> Unit
) {
    var listName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create shopping list")

        TextField(
            value = listName,
            onValueChange = { listName = it },
            label = { Text("List name") }
        )

        Button(
            onClick = { onListCreated(1) },
            enabled = listName.isNotBlank()
        ) {
            Text("Create")
        }

        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}
