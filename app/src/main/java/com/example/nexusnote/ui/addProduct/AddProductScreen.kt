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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexusnote.R

private val VerdeFondo = Color(0xFFF0FFF5)
private val VerdePrincipal = Color(0xFF00A651)
private val AzulTexto = Color(0xFF607D8B)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: AddProductViewModel,
    onBackClick: () -> Unit = {},
    onAddProduct: (String, Int, String) -> Unit = { _, _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsState()
    var menuExpandido by remember { mutableStateOf(false) }
    var mostrarSelectorImagen by remember { mutableStateOf(false) }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imagenUri = uri
            viewModel.seleccionarImagen("Galería")
            mostrarSelectorImagen = false
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


    Scaffold(
        containerColor = VerdeFondo,
        topBar = {
            TopAppBar(
                title = {
                    Text("Agregar producto", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(color = Color.White, shadowElevation = 6.dp) {
                Button(
                    onClick = {
                        if (viewModel.validarFormulario()) {
                            onAddProduct(uiState.nombre.trim(), uiState.cantidad, uiState.unidad)
                        }
                    },
                    enabled = uiState.formularioValido,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
                ) {
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
                        .background(Color(0xFFF5F5F7)),
                    contentAlignment = Alignment.Center
                ) {
                    val imagenRecurso = imagenesPredeterminadas[uiState.imagenSeleccionada]
                    when {
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
                                    tint = Color.Gray,
                                    modifier = Modifier.size(35.dp)
                                )
                                Text("Sin foto", fontSize = 11.sp, color = Color.LightGray)
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
                        .background(VerdePrincipal, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Seleccionar imagen", tint = Color.White)
                }
            }

            if (mostrarSelectorImagen) {
                Spacer(Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "SELECCIONAR IMAGEN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulTexto
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { /* Cámara pendiente de implementar */ },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) { Text("📷 Cámara", fontSize = 12.sp) }
                            OutlinedButton(
                                onClick = { galeriaLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f)
                            ) { Text("Galería", fontSize = 12.sp) }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("O elige una imagen de muestra:", fontSize = 11.sp, color = Color.Gray)
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
                                            viewModel.seleccionarImagen(categoria)
                                            imagenUri = null
                                            mostrarSelectorImagen = false
                                        },
                                        modifier = Modifier.weight(1f).height(90.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        border = if (seleccionada) BorderStroke(2.dp, VerdePrincipal) else null,
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (seleccionada) Color(0xFFE0F7E9)
                                            else Color(0xFFF5F5F5)
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
                                                    tint = Color.Gray,
                                                    modifier = Modifier.align(Alignment.Center).size(30.dp)
                                                )
                                            }
                                            Text(
                                                text = categoria,
                                                fontSize = 10.sp,
                                                color = if (imagen != null) Color.White else Color.DarkGray,
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
                color = AzulTexto,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = viewModel::actualizarNombre,
                placeholder = { Text("Ej: Leche entera...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerdePrincipal,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
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
                color = AzulTexto,
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
                        .background(Color.White, RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(14.dp)),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = viewModel::disminuirCantidad) {
                        Icon(Icons.Default.Remove, contentDescription = "Disminuir cantidad")
                    }
                    Text(uiState.cantidad.toString(), fontWeight = FontWeight.Bold)
                    IconButton(onClick = viewModel::aumentarCantidad) {
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
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
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
                                    viewModel.seleccionarUnidad(opcion)
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

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddProductScreenPreview() {
    val previewViewModel = remember { AddProductViewModel() }
    AddProductScreen(viewModel = previewViewModel)
}
