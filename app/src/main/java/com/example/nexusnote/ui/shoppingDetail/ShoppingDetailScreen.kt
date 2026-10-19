package com.example.nexusnote.ui.shoppingDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter

private val Green = Color(0xFF11A34A)
private val LightBackground = Color(0xFFEFFCF3)
private val GrayText = Color(0xFF94A3B8)
private val DarkText = Color(0xFF101827)

@Composable
fun ShoppingDetailScreen(
    uiState: ShoppingDetailUiState,
    onBack: () -> Unit = {},
    onAddProduct: () -> Unit = {},
    onToggleProduct: (Int) -> Unit,
    onQuantityChange: (Int, Int) -> Unit,
    onDeleteProduct: (Int) -> Unit
) {
    ShoppingDetailContent(
        uiState = uiState,
        onBack = onBack,
        onAddProduct = onAddProduct,
        onToggleProduct = onToggleProduct,
        onQuantityChange = onQuantityChange,
        onDeleteProduct = onDeleteProduct
    )
}

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

        // Encabezado verde
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Green,
                    shape = RoundedCornerShape(
                        bottomStart = 28.dp,
                        bottomEnd = 28.dp
                    )
                )
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 36.dp,
                    bottom = 20.dp
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                Column {
                    Text(
                        text = "🛒 ${uiState.listName}",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = uiState.dateLabel,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${uiState.purchasedCount} de " +
                            "${uiState.totalCount} comprados",
                    color = Color.White,
                    fontSize = 13.sp
                )

                Text(
                    text = "${uiState.percentage}%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { uiState.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }

        // Contenido de la pantalla
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Green
                    )
                }

                uiState.errorMessage != null &&
                        uiState.products.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "No se pudieron cargar los productos",
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )

                        Text(
                            text = uiState.errorMessage,
                            color = GrayText,
                            fontSize = 13.sp
                        )
                    }
                }

                uiState.products.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "🛒",
                            fontSize = 48.sp
                        )

                        Text(
                            text = "Tu lista está vacía",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )

                        Text(
                            text = "Todavía no hay productos. " +
                                    "Puedes agregar el primero.",
                            color = GrayText,
                            fontSize = 13.sp
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        if (uiState.errorMessage != null) {
                            item {
                                Text(
                                    text = uiState.errorMessage,
                                    color =
                                        MaterialTheme.colorScheme.error,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        item {
                            SectionTitle(
                                "PENDIENTES " +
                                        "(${uiState.pendingProducts.size})"
                            )
                        }

                        items(
                            items = uiState.pendingProducts,
                            key = { "pending_${it.id}" }
                        ) { product ->
                            ProductCard(
                                product = product,
                                onToggle = {
                                    onToggleProduct(product.id)
                                },
                                onQuantityChange = { quantity ->
                                    onQuantityChange(
                                        product.id,
                                        quantity
                                    )
                                },
                                onDelete = {
                                    onDeleteProduct(product.id)
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            SectionTitle(
                                "COMPRADOS " +
                                        "(${uiState.purchasedProducts.size})"
                            )
                        }

                        items(
                            items = uiState.purchasedProducts,
                            key = { "purchased_${it.id}" }
                        ) { product ->
                            ProductCard(
                                product = product,
                                onToggle = {
                                    onToggleProduct(product.id)
                                },
                                onQuantityChange = { quantity ->
                                    onQuantityChange(
                                        product.id,
                                        quantity
                                    )
                                },
                                onDelete = {
                                    onDeleteProduct(product.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Botón para navegar a Agregar producto
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
                text = "＋  Agregar producto",
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
private fun ProductPhoto(
    product: ShoppingDetailProduct
) {
    var imageFailed by remember(product.photoUri) {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .size(49.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F5F9)),
        contentAlignment = Alignment.Center
    ) {
        if (!product.photoUri.isNullOrBlank() && !imageFailed) {
            AsyncImage(
                model = product.photoUri,
                contentDescription = "Foto de ${product.name}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onState = { state ->
                    if (state is AsyncImagePainter.State.Error) {
                        imageFailed = true
                    }
                }
            )
        } else {
            Text(
                text = product.emoji,
                fontSize = 27.sp
            )
        }
    }
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

            // Marcar como comprado
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (product.purchased) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Green, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Comprado",
                            tint = Color.White
                        )
                    }
                } else {
                    RadioButton(
                        selected = false,
                        onClick = onToggle,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(9.dp))

            // Fotografía o imagen de respaldo
            ProductPhoto(product = product)

            Spacer(modifier = Modifier.width(12.dp))

            // Nombre y cantidad
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    color = if (product.purchased) {
                        GrayText
                    } else {
                        DarkText
                    },
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    textDecoration = if (product.purchased) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    }
                )

                Text(
                    text = "${product.quantity} ${product.unit}",
                    color = GrayText,
                    fontSize = 12.sp
                )
            }

            if (product.purchased) {
                Text(
                    text = "✓ Listo",
                    color = Green,
                    fontSize = 12.sp
                )
            } else {
                Text(
                    text = "Pendiente",
                    color = GrayText,
                    fontSize = 11.sp
                )
            }

            // Opciones del producto
            Box {
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones del producto",
                        tint = GrayText
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
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

// Vista previa independiente de Room
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ShoppingDetailScreenPreview() {
    val previewState = ShoppingDetailUiState(
        listId = 1,
        listName = "Súper semanal",
        dateLabel = "Hoy",
        products = listOf(
            ShoppingDetailProduct(
                1, "Leche entera", 2, "unidades", "🥛"
            ),
            ShoppingDetailProduct(
                2, "Pan integral", 1, "unidades", "🍞"
            ),
            ShoppingDetailProduct(
                3, "Huevos", 12, "unidades", "🥚"
            ),
            ShoppingDetailProduct(
                4, "Queso manchego", 200, "unidades", "🧀"
            ),
            ShoppingDetailProduct(
                5, "Manzanas rojas", 6, "unidades", "🍎", true
            ),
            ShoppingDetailProduct(
                6, "Tomates", 5, "unidades", "🍅", true
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
