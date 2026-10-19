package com.example.nexusnote.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/** Stateless persistence boundary: the dialog edits a draft and emits one validated total. */
@Composable
fun QuantityDialog(
    title: String,
    description: String,
    initialValue: Int,
    minimum: Int,
    maximum: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var input by rememberSaveable(initialValue) { mutableStateOf(initialValue.toString()) }
    val amount = input.toIntOrNull()
    val valid = amount != null && amount in minimum..maximum
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(description)
                OutlinedTextField(
                    value = input,
                    onValueChange = { if (it.length <= 10 && it.all { c -> c in '0'..'9' }) input = it },
                    label = { Text("Cantidad total") },
                    supportingText = { Text("Entre $minimum y $maximum. Puedes escribir el número directamente.") },
                    isError = !valid,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(5, 10).forEach { step ->
                        OutlinedButton(onClick = {
                            input = ((amount ?: minimum).toLong() + step)
                                .coerceIn(minimum.toLong(), maximum.toLong()).toString()
                        }, enabled = (amount ?: minimum) < maximum) { Text("+$step") }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { amount?.takeIf { valid }?.let(onConfirm) }, enabled = valid) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
