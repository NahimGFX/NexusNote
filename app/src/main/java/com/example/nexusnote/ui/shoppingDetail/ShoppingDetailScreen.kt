package com.example.nexusnote.ui.shoppingDetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

data class ShoppingProductUi(
    val id: Int,
    val name: String,
    val quantity: Int,
    val purchased: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingDetailScreen(
    listId: Int,
    onBack: () -> Unit = {}
) {
    var products by remember(listId) {
        mutableStateOf(
            listOf(
                ShoppingProductUi(1, "Leche", 2),
                ShoppingProductUi(2, "Arroz", 1),
                ShoppingProductUi(3, "Manzanas", 5),
                ShoppingProductUi(4, "Pan", 3)
            )
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newProductName by remember { mutableStateOf("") }
    var newProductQuantity by remember { mutableStateOf("1") }

    val purchasedCount = products.count { it.purchased }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de compras") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, "Agregar producto")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Mi lista de compras",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$purchasedCount de ${products.size} productos comprados",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = {
                    if (products.isEmpty()) 0f
                    else purchasedCount.toFloat() / products.size
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (products.isEmpty()) {
                Text("Todavía no hay productos en esta lista.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(products, key = { it.id }) { product ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = product.purchased,
                                    onCheckedChange = { checked ->
                                        products = products.map {
                                            if (it.id == product.id)
                                                it.copy(purchased = checked)
                                            else it
                                        }
                                    }
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        product.name,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text("Cantidad: ${product.quantity}")
                                }

                                IconButton(
                                    onClick = {
                                        products = products.map {
                                            if (it.id == product.id) {
                                                it.copy(
                                                    quantity = (it.quantity - 1)
                                                        .coerceAtLeast(1)
                                                )
                                            } else it
                                        }
                                    }
                                ) {
                                    Text("−")
                                }

                                Text("${product.quantity}")

                                IconButton(
                                    onClick = {
                                        products = products.map {
                                            if (it.id == product.id)
                                                it.copy(quantity = it.quantity + 1)
                                            else it
                                        }
                                    }
                                ) {
                                    Text("+")
                                }

                                IconButton(
                                    onClick = {
                                        products = products.filterNot {
                                            it.id == product.id
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar producto"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Agregar producto") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newProductName,
                        onValueChange = { newProductName = it },
                        label = { Text("Nombre del producto") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newProductQuantity,
                        onValueChange = { newProductQuantity = it },
                        label = { Text("Cantidad") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val quantity = newProductQuantity.toIntOrNull()

                        if (newProductName.isNotBlank() &&
                            quantity != null && quantity > 0
                        ) {
                            val nextId =
                                (products.maxOfOrNull { it.id } ?: 0) + 1

                            products = products + ShoppingProductUi(
                                id = nextId,
                                name = newProductName.trim(),
                                quantity = quantity
                            )

                            newProductName = ""
                            newProductQuantity = "1"
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShoppingDetailScreenPreview() {
    MaterialTheme {
        ShoppingDetailScreen(
            listId = 1,
            onBack = {}
        )
    }
}