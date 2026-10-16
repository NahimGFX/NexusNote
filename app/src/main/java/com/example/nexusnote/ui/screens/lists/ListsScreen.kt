package com.example.nexusnote.ui.screens.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Punto inicial del paquete de la pantalla de listas. */
@Composable
fun ListsScreen(
    onCreateListClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Mis listas",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Aquí se mostrarán tus listas de compras.",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(onClick = onCreateListClick) {
                Text("Nueva lista")
            }
        }
    }
}
