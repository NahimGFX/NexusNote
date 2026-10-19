package com.example.nexusnote.ui.addProduct

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexusnote.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.rememberCoroutineScope
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.UUID
import android.webkit.MimeTypeMap

private val categorias = listOf(
    "Frutas y verduras", "Lácteos", "Panadería", "Carnes", "Bebidas", "Sin foto"
)
private val unidades = listOf("pzas", "kg", "g", "litros", "ml", "paquetes", "latas")

private suspend fun copyUriToAppStorage(
    context: Context,
    sourceUri: Uri
): String = withContext(Dispatchers.IO) {
    val mimeType = context.contentResolver.getType(sourceUri)
    val extension = MimeTypeMap.getSingleton()
        .getExtensionFromMimeType(mimeType)
        ?.takeIf { it.matches(Regex("[A-Za-z0-9]+")) }
        ?: "img"

    copyImageToAppStorage(context, extension) {
        context.contentResolver.openInputStream(sourceUri)
    }
}

private suspend fun copyFileToAppStorage(
    context: Context,
    sourceFile: File
): String = withContext(Dispatchers.IO) {
    copyImageToAppStorage(context, "jpg") {
        sourceFile.inputStream()
    }
}

private fun copyImageToAppStorage(
    context: Context,
    extension: String,
    openSource: () -> java.io.InputStream?
): String {
    val imageDirectory = File(context.filesDir, "product_images")
    if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
        throw IOException("No se pudo crear el almacenamiento de imágenes")
    }

    val temporaryFile = File.createTempFile("product-", ".tmp", imageDirectory)
    val durableFile = File(imageDirectory, "${UUID.randomUUID()}.$extension")
    return try {
        val input = openSource()
            ?: throw IOException("No se pudo leer la imagen seleccionada")
        input.use { source ->
            temporaryFile.outputStream().buffered().use { destination ->
                source.copyTo(destination)
            }
        }
        if (!temporaryFile.renameTo(durableFile)) {
            throw IOException("No se pudo guardar la imagen seleccionada")
        }
        durableFile.absolutePath
    } catch (error: Exception) {
        durableFile.delete()
        throw error
    } finally {
        temporaryFile.delete()
    }
}

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
    onPhotoUriChange: (String?) -> Unit = {},
    onImageError: () -> Unit = {},
    onAddProduct: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isImageProcessing by remember { mutableStateOf(false) }
    var cameraTempPath by rememberSaveable { mutableStateOf<String?>(null) }

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            isImageProcessing = true
            scope.launch {
                try {
                    val durablePath = copyUriToAppStorage(context, uri)
                    onImagenChange("Galería")
                    onPhotoUriChange(durablePath)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    onImageError()
                } finally {
                    isImageProcessing = false
                }
            }
        }
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { captured ->
        val tempFile = cameraTempPath?.let(::File)
        cameraTempPath = null
        if (captured && tempFile != null) {
            isImageProcessing = true
            scope.launch {
                try {
                    val durablePath = copyFileToAppStorage(context, tempFile)
                    onImagenChange("Cámara")
                    onPhotoUriChange(durablePath)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    onImageError()
                } finally {
                    tempFile.delete()
                    isImageProcessing = false
                }
            }
        } else {
            tempFile?.delete()
        }
    }

    AddProductContent(
        uiState = uiState,
        isImageProcessing = isImageProcessing,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onNombreChange = onNombreChange,
        onAumentarCantidad = onAumentarCantidad,
        onDisminuirCantidad = onDisminuirCantidad,
        onUnidadChange = onUnidadChange,
        onImagenChange = { categoria ->
            onImagenChange(categoria)
            if (categoria in categorias) {
                onPhotoUriChange(
                    ProductImageReference.referenceForCategory(categoria)
                )
            }
        },
        onAbrirCamara = {
            try {
                val cameraDirectory = File(context.cacheDir, "camera_captures")
                if (!cameraDirectory.exists() && !cameraDirectory.mkdirs()) {
                    throw IOException("No se pudo preparar la captura de cámara")
                }
                val tempFile = File.createTempFile(
                    "product-camera-",
                    ".jpg",
                    cameraDirectory
                )
                cameraTempPath = tempFile.absolutePath
                val outputUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    tempFile
                )
                camaraLauncher.launch(outputUri)
            } catch (_: Exception) {
                cameraTempPath?.let(::File)?.delete()
                cameraTempPath = null
                onImageError()
            }
        },
        onAbrirGaleria = {
            if (!isImageProcessing) {
                galeriaLauncher.launch(arrayOf("image/*"))
            }
        },
        onAgregarProducto = onAddProduct
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProductContent(
    uiState: AddProductUiState,
    isImageProcessing: Boolean,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onNombreChange: (String) -> Unit,
    onAumentarCantidad: () -> Unit,
    onDisminuirCantidad: () -> Unit,
    onUnidadChange: (String) -> Unit,
    onImagenChange: (String) -> Unit,
    onAbrirCamara: () -> Unit,
    onAbrirGaleria: () -> Unit,
    onAgregarProducto: () -> Unit
) {
    var menuExpandido by remember { mutableStateOf(false) }
    var mostrarSelectorImagen by remember { mutableStateOf(false) }
    var imageLoadFailed by remember(uiState.photoUri) { mutableStateOf(false) }

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
                    enabled = uiState.formularioValido &&
                            !uiState.isSaving && !isImageProcessing,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (uiState.isSaving || isImageProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    Text(
                        text = if (isImageProcessing && !uiState.isSaving) {
                            "Preparando imagen..."
                        } else {
                            "Agregar producto"
                        },
                        fontWeight = FontWeight.Bold
                    )
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
                    val imagenRecurso =
                        ProductImageReference.drawableResourceId(uiState.photoUri)
                            ?: ProductImageReference
                                .forCategory(uiState.imagenSeleccionada)
                                ?.drawableRes
                    when {
                        imagenRecurso != null -> {
                            Image(
                                painter = painterResource(imagenRecurso),
                                contentDescription = uiState.imagenSeleccionada,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        !uiState.photoUri.isNullOrBlank() &&
                                !ProductImageReference.isResourceReference(uiState.photoUri) &&
                                !imageLoadFailed -> {
                            AsyncImage(
                                model = File(uiState.photoUri),
                                contentDescription = "Foto seleccionada del producto",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                onState = { state ->
                                    imageLoadFailed =
                                        state is AsyncImagePainter.State.Error
                                }
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
                                            val imagen = ProductImageReference
                                                .forCategory(categoria)
                                                ?.drawableRes
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
