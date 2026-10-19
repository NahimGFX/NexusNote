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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import java.io.File

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
            .background(MaterialTheme.colorScheme.background)
    ) {

        // Encabezado verde
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
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
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Column {
                    Text(
                        text = "🛒 ${uiState.listName}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = uiState.dateLabel,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )

                Text(
                    text = "${uiState.percentage}%",
                    color = MaterialTheme.colorScheme.primary,
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
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface
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
                        color = MaterialTheme.colorScheme.primary
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
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Todavía no hay productos. " +
                                    "Puedes agregar el primero.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
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
        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (!product.photoUri.isNullOrBlank() && !imageFailed) {
            AsyncImage(
                model = product.photoUri.let { storedImage ->
                    if (storedImage.startsWith('/')) File(storedImage) else storedImage
                },
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
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
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
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Comprado",
                            tint = MaterialTheme.colorScheme.onPrimary
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
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            if (product.purchased) {
                Text(
                    text = "✓ Listo",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp
                )
            } else {
                Text(
                    text = "Pendiente",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
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
