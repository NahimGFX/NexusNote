
package com.example.nexusnote.ui.shoppingDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val Green = Color(0xFF11A34A)
private val LightBackground = Color(0xFFEFFCF3)
private val GrayText = Color(0xFF94A3B8)

// Conecta la pantalla con el ViewModel
@Composable
fun ShoppingDetailScreen(
    listId: Int,
    onBack: () -> Unit = {},
    onAddProduct: () -> Unit = {},
    viewModel: ShoppingDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(listId) {
        viewModel.loadList(listId)
    }

    ShoppingDetailContent(
        uiState = uiState,
        onBack = onBack,
        onAddProduct = onAddProduct,
        onToggleProduct = viewModel::togglePurchased,
        onQuantityChange = viewModel::updateQuantity,
        onDeleteProduct = viewModel::deleteProduct
    )
}

// Interfaz visual de detalle de compras
@Composable
private fun ShoppingDetailContent(
    uiState: ShoppingDetailUiState,
    onBack: () -> Unit,
    onAddProduct: () -> Unit,
    onToggleProduct: (Int) -> Unit,
    onQuantityChange: (Int, Int) -> Unit,
    onDeleteProduct: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Green,
                    RoundedCornerShape(
                        bottomStart = 28.dp,
                        bottomEnd = 28.dp
                    )
                )
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 45.dp,
                    bottom = 20.dp
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                Column {
                    Text(
                        text = "🛒 ${uiState.listName}",
                        color = Color.White,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.dateLabel,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${uiState.purchasedCount} de ${uiState.totalCount} comprados",
                    color = Color.White,
                    fontSize = 13.sp
                )

                Text(
                    "${uiState.percentage}%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(7.dp))

            LinearProgressIndicator(
                progress = { uiState.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Green)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (uiState.errorMessage != null) {
                    item {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                item {
                    SectionTitle(
                        "PENDIENTES (${uiState.pendingProducts.size})"
                    )
                }

                items(
                    items = uiState.pendingProducts,
                    key = { it.id }
                ) { product ->
                    ProductCard(
                        product = product,
                        onToggle = {
                            onToggleProduct(product.id)
                        },
                        onQuantityChange = { quantity ->
                            onQuantityChange(product.id, quantity)
                        },
                        onDelete = {
                            onDeleteProduct(product.id)
                        }
                    )
                }

                item {
                    Spacer(Modifier.height(10.dp))
                    SectionTitle(
                        "COMPRADOS (${uiState.purchasedProducts.size})"
                    )
                }

                items(
                    items = uiState.purchasedProducts,
                    key = { it.id }
                ) { product ->
                    ProductCard(
                        product = product,
                        onToggle = {
                            onToggleProduct(product.id)
                        },
                        onQuantityChange = { quantity ->
                            onQuantityChange(product.id, quantity)
                        },
                        onDelete = {
                            onDeleteProduct(product.id)
                        }
                    )
                }

                if (uiState.products.isEmpty()) {
                    item {
                        Text(
                            "Esta lista todavía no tiene productos.",
                            color = GrayText,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Button(
            onClick = onAddProduct,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 14.dp
                )
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Green
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                "＋  Agregar producto",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = GrayText,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(
            top = 8.dp,
            bottom = 5.dp
        )
    )
}

@Composable
private fun ProductCard(
    product: ShoppingDetailProduct,
    onToggle: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        if (product.purchased) Green
                        else Color.White,
                        CircleShape
                    )
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (product.purchased) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Comprado",
                        tint = Color.White
                    )
                } else {
                    RadioButton(
                        selected = false,
                        onClick = onToggle,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(start = 9.dp)
                    .size(49.dp)
                    .background(
                        Color(0xFFF1F5F9),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    product.emoji,
                    fontSize = 27.sp
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = product.name,
                    color = if (product.purchased)
                        GrayText else Color(0xFF101827),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    textDecoration = if (product.purchased)
                        TextDecoration.LineThrough
                    else TextDecoration.None
                )

                Text(
                    "${product.quantity} ${product.unit}",
                    color = GrayText,
                    fontSize = 12.sp
                )
            }

            if (product.purchased) {
                Text(
                    "✓ Listo",
                    color = Green,
                    fontSize = 12.sp
                )
            } else {
                Text(
                    "Pendiente",
                    color = GrayText,
                    fontSize = 11.sp
                )
            }

            Box {
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Opciones",
                        tint = GrayText
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Aumentar cantidad") },
                        onClick = {
                            onQuantityChange(product.quantity + 1)
                            expanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Disminuir cantidad") },
                        onClick = {
                            onQuantityChange(
                                (product.quantity - 1)
                                    .coerceAtLeast(1)
                            )
                            expanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Eliminar producto") },
                        onClick = {
                            onDelete()
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShoppingDetailScreenPreview() {
    val previewState = ShoppingDetailUiState(
        listId = 1,
        products = listOf(
            ShoppingDetailProduct(
                1, "Leche entera", 2, "litros", "🥛"
            ),
            ShoppingDetailProduct(
                2, "Pan integral", 1, "pieza", "🍞"
            ),
            ShoppingDetailProduct(
                3, "Huevos", 12, "pzas", "🥚"
            ),
            ShoppingDetailProduct(
                4, "Queso manchego", 200, "g", "🧀"
            ),
            ShoppingDetailProduct(
                5, "Manzanas rojas", 6, "pzas", "🍎", true
            ),
            ShoppingDetailProduct(
                6, "Tomates", 500, "g", "🍅", true
            )
        )
    )

    MaterialTheme {
        ShoppingDetailContent(
            uiState = previewState,
            onBack = {},
            onAddProduct = {},
            onToggleProduct = {},
            onQuantityChange = { _, _ -> },
            onDeleteProduct = {}
        )
    }
}