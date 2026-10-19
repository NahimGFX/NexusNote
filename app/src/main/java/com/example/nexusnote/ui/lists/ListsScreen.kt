package com.example.nexusnote.ui.screens.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nexusnote.viewmodel.ListsUiState
import com.example.nexusnote.viewmodel.ShoppingListUi

/** Composable sin ViewModel: solo recibe estado inmutable y eventos. */
@Composable
fun ListsScreen(
    uiState: ListsUiState,
    onOpenList: (Int) -> Unit,
    onDeleteList: (Int) -> Unit,
    onCreateClick: () -> Unit,
    onRetryClick: () -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentLists = (uiState as? ListsUiState.Success)?.lists.orEmpty()
    var pendingDeleteId by rememberSaveable { mutableStateOf<Int?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ListsHeader(lists = currentLists, onBackClick = onBackClick)

            when (val state = uiState) {
                ListsUiState.Loading -> LoadingContent(Modifier.weight(1f))
                is ListsUiState.Error -> ErrorContent(
                    message = state.message,
                    onRetryClick = onRetryClick,
                    modifier = Modifier.weight(1f)
                )
                is ListsUiState.Success -> {
                    if (state.lists.isEmpty()) {
                        EmptyContent(Modifier.weight(1f))
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                top = 14.dp,
                                end = 16.dp,
                                bottom = 12.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.lists, key = { it.id }) { list ->
                                ShoppingListCard(
                                    list = list,
                                    onOpenClick = { onOpenList(list.id) },
                                    onDeleteClick = { pendingDeleteId = list.id }
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onCreateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("＋   Nueva lista", fontWeight = FontWeight.SemiBold)
            }
        }
    }

    val listToDelete = currentLists.firstOrNull { it.id == pendingDeleteId }
    if (listToDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("Eliminar lista") },
            text = { Text("¿Quieres eliminar la lista ‘${listToDelete.name}’?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteList(listToDelete.id)
                    pendingDeleteId = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun ListsHeader(lists: List<ShoppingListUi>, onBackClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp)) {
        TextButton(onClick = onBackClick) { Text("‹ Inicio") }
        Text("Mis listas", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("${lists.size} listas · Organiza, abre o elimina tus compras",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ShoppingListCard(
    list: ShoppingListUi,
    onOpenClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val productCount = list.productCount.coerceAtLeast(0)
    val purchasedCount = list.purchasedCount.coerceIn(0, productCount)
    val progress = list.progressPercent.coerceIn(0, 100)
    val listEmoji = list.icon?.takeIf { it.isNotBlank() } ?: "🛒"

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) { Text(listEmoji, style = MaterialTheme.typography.titleLarge) }

                Column(
                    modifier = Modifier.weight(1f).padding(start = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(list.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(
                        "${list.dateLabel}  ·  $productCount productos",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                TextButton(onClick = onDeleteClick) {
                    Text("🗑", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (productCount == 0) {
                Text(
                    "Lista vacía",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "$purchasedCount de $productCount productos completos",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        "$progress%",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                if (list.productPreviewEmojis.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val previews = list.productPreviewEmojis.take(5)
                        previews.forEach { productEmoji ->
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) { Text(productEmoji, style = MaterialTheme.typography.bodyMedium) }
                        }
                        val extraCount = productCount - previews.size
                        if (extraCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) { Text("+$extraCount", style = MaterialTheme.typography.labelSmall) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("🛒", style = MaterialTheme.typography.headlineLarge)
        Text("Todavía no tienes listas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(
            "Crea una lista para empezar a organizar tus compras.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(12.dp))
        Text("Cargando listas…")
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("No se pudieron cargar las listas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(message, modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Button(onClick = onRetryClick, modifier = Modifier.padding(top = 12.dp)) {
            Text("Reintentar")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListsScreenPreview() {
    MaterialTheme {
        ListsScreen(
            uiState = ListsUiState.Success(
                lists = listOf(
                    ShoppingListUi(1, "Súper semanal", "Hoy", 2, 6, listOf("🍎", "🥛", "🍞", "🍅", "🥚")),
                    ShoppingListUi(2, "Desayuno especial", "Ayer", 1, 2, listOf("🍌", "🥣")),
                    ShoppingListUi(3, "Fiesta de cumpleaños", "Hace 3 días", 0, 0)
                )
            ),
            onOpenList = {},
            onDeleteList = {},
            onCreateClick = {},
            onRetryClick = {}
        )
    }
}
