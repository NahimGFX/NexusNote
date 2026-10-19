package com.example.nexusnote.ui.addProduct

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexusnote.R
import androidx.compose.ui.text.TextStyle

private val imagenesPredeterminadas = mapOf(
    "Frutas y verduras" to R.drawable.frutas_verduras,
    "Lácteos" to R.drawable.lacteos,
    "Panadería" to R.drawable.panaderia,
    "Carnes" to R.drawable.carnes,
    "Bebidas" to R.drawable.bebidas
)
private val categorias = listOf(
    "Frutas y verduras", "Lácteos", "Panadería", "Carnes", "Bebidas", "Sin foto"
)
private val unidades = listOf("pzas", "kg", "g", "litros", "ml", "paquetes", "latas")

@Composable
fun AddProductScreen(
    uiState: AddProductUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit = {},
    onNombreChange: (String) -> Unit,
    onAumentarCantidad: () -> Unit,
    onDisminuirCantidad: () -> Unit,
    onUnidadChange: (String) -> Unit,
    onImagenChange: (String) -> Unit,
    onAddProduct: () -> Unit
) {
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var fotoCamara by remember { mutableStateOf<Bitmap?>(null) }

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imagenUri = uri
            fotoCamara = null
            onImagenChange("Galería")
        }
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            fotoCamara = bitmap
            imagenUri = null
            onImagenChange("Cámara")
        }
    }

    val context = LocalContext.current
    val bitmapSeleccionado = remember(imagenUri) {
        imagenUri?.let { uri ->
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }

    AddProductContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onNombreChange = onNombreChange,
        onAumentarCantidad = onAumentarCantidad,
        onDisminuirCantidad = onDisminuirCantidad,
        onUnidadChange = onUnidadChange,
        onImagenChange = { categoria ->
            onImagenChange(categoria)
            imagenUri = null
            fotoCamara = null
        },
        onAbrirCamara = { camaraLauncher.launch(null) },
        onAbrirGaleria = { galeriaLauncher.launch("image/*") },
        fotoCamara = fotoCamara?.asImageBitmap(),
        bitmapSeleccionado = bitmapSeleccionado,
        onAgregarProducto = onAddProduct
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProductContent(
    uiState: AddProductUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onNombreChange: (String) -> Unit,
    onAumentarCantidad: () -> Unit,
    onDisminuirCantidad: () -> Unit,
    onUnidadChange: (String) -> Unit,
    onImagenChange: (String) -> Unit,
    onAbrirCamara: () -> Unit,
    onAbrirGaleria: () -> Unit,
    fotoCamara: ImageBitmap?,
    bitmapSeleccionado: ImageBitmap?,
    onAgregarProducto: () -> Unit
) {
    var menuExpandido by remember { mutableStateOf(false) }
    var mostrarSelectorImagen by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Agregar producto",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp
            ) {
                Button(
                    onClick = {
                        onAgregarProducto()
                    },
                    enabled = uiState.formularioValido && !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    Text("Agregar producto", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen principal y único botón verde de cámara
            Box(
                modifier = Modifier.size(width = 120.dp, height = 112.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    val imagenRecurso = imagenesPredeterminadas[uiState.imagenSeleccionada]
                    when {
                        uiState.imagenSeleccionada == "Cámara" && fotoCamara != null -> {
                            Image(
                                bitmap = fotoCamara,
                                contentDescription = "Fotografía tomada con la cámara",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        uiState.imagenSeleccionada == "Galería" && bitmapSeleccionado != null -> {
                            Image(
                                bitmap = bitmapSeleccionado,
                                contentDescription = "Imagen seleccionada de galería",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        imagenRecurso != null -> {
                            Image(
                                painter = painterResource(imagenRecurso),
                                contentDescription = uiState.imagenSeleccionada,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Sin foto",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(35.dp)
                                )
                                Text(
                                    "Sin foto",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                IconButton(
                    onClick = { mostrarSelectorImagen = !mostrarSelectorImagen },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(x = 38.dp, y = 72.dp)
                        .size(38.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Seleccionar imagen",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            if (mostrarSelectorImagen) {
                Spacer(Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "SELECCIONAR IMAGEN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { mostrarSelectorImagen = false; onAbrirCamara() },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) { Text("📷 Cámara", fontSize = 12.sp) }
                            OutlinedButton(
                                onClick = { mostrarSelectorImagen = false; onAbrirGaleria() },
                                modifier = Modifier.weight(1f)
                            ) { Text("Galería", fontSize = 12.sp) }
                        }
                        Spacer(Modifier.height(12.dp))
                            Text(
                                "O elige una imagen de muestra:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        Spacer(Modifier.height(10.dp))

                        categorias.chunked(3).forEach { fila ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                fila.forEach { categoria ->
                                    val seleccionada = uiState.imagenSeleccionada == categoria
                                    Card(
                                        onClick = {
                                            onImagenChange(categoria)
                                            mostrarSelectorImagen = false
                                        },
                                        modifier = Modifier.weight(1f).height(90.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        border = if (seleccionada) {
                                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                        } else {
                                            null
                                        },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (seleccionada) {
                                                MaterialTheme.colorScheme.primaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.BottomCenter
                                        ) {
                                            val imagen = imagenesPredeterminadas[categoria]
                                            if (imagen != null) {
                                                Image(
                                                    painter = painterResource(imagen),
                                                    contentDescription = categoria,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else {
                                                Icon(
                                                    Icons.Default.ShoppingCart,
                                                    contentDescription = "Sin foto",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.align(Alignment.Center).size(30.dp)
                                                )
                                            }
                                            Text(
                                                text = categoria,
                                                fontSize = 10.sp,
                                                color = if (imagen != null) {
                                                    MaterialTheme.colorScheme.onPrimary
                                                } else {
                                                    MaterialTheme.colorScheme.onSurface
                                                },
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(
                                                        if (imagen != null) Color.Black.copy(alpha = 0.55f)
                                                        else Color.Transparent
                                                    )
                                                    .padding(3.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
            Text(
                "NOMBRE DEL PRODUCTO",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = onNombreChange,
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                placeholder = { Text("Ej: Leche entera...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            uiState.errorNombre?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(
                "CANTIDAD",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.shapes.medium
                        )
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            MaterialTheme.shapes.medium
                        ),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDisminuirCantidad) {
                        Icon(Icons.Default.Remove, contentDescription = "Disminuir cantidad")
                    }
                    Text(uiState.cantidad.toString(), fontWeight = FontWeight.Bold)
                    IconButton(onClick = onAumentarCantidad) {
                        Icon(Icons.Default.Add, contentDescription = "Aumentar cantidad")
                    }
                }
                ExposedDropdownMenuBox(
                    expanded = menuExpandido,
                    onExpandedChange = { menuExpandido = !menuExpandido },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = uiState.unidad,
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(
                                ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            ),
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = menuExpandido,
                        onDismissRequest = { menuExpandido = false }
                    ) {
                        unidades.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    onUnidadChange(opcion)
                                    menuExpandido = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
