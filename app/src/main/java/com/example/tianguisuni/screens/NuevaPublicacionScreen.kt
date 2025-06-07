package com.example.tianguisuni.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.isSystemInDarkTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaPublicacionScreen(
    onClose: () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val greenColor = Color(0xFF669C4A)
    
    // Configurar colores de la barra de estado y navegación
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = greenColor,
            darkIcons = false
        )
    }

    var nombreProducto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var expandidoCategoria by remember { mutableStateOf(false) }

    // Estados para los errores
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorCategoria by remember { mutableStateOf<String?>(null) }
    var errorDescripcion by remember { mutableStateOf<String?>(null) }
    var errorUbicacion by remember { mutableStateOf<String?>(null) }
    var errorPrecio by remember { mutableStateOf<String?>(null) }

    val categorias = listOf("Dulces", "Comida", "Bebidas", "Ropa", "Regalos", "Otros")

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = greenColor
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Nueva Publicación",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    // Espacio para mantener el título centrado
                    Box(modifier = Modifier.size(48.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(bottom = 80.dp) // Padding extra al final para asegurar que todo sea visible
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Bottom
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Nombre del producto
                Text(
                    text = "Nombre del producto",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = nombreProducto,
                    onValueChange = { 
                        if (it.length <= 30) {
                            nombreProducto = it
                            errorNombre = null
                        } else {
                            errorNombre = "Máximo 30 caracteres"
                        }
                    },
                    placeholder = { Text("Escribe el nombre de tu producto") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF669C4A),
                        errorBorderColor = Color.Red
                    ),
                    singleLine = true,
                    isError = errorNombre != null,
                    supportingText = { errorNombre?.let { Text(it, color = Color.Red) } }
                )

                // Categoría
                Text(
                    text = "Categoria",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                ExposedDropdownMenuBox(
                    expanded = expandidoCategoria,
                    onExpandedChange = { expandidoCategoria = it }
                ) {
                    OutlinedTextField(
                        value = categoriaSeleccionada,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Selecciona categoria") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = Color(0xFF669C4A),
                            errorBorderColor = Color.Red
                        ),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoCategoria) },
                        isError = errorCategoria != null,
                        supportingText = { errorCategoria?.let { Text(it, color = Color.Red) } }
                    )
                    ExposedDropdownMenu(
                        expanded = expandidoCategoria,
                        onDismissRequest = { expandidoCategoria = false }
                    ) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria) },
                                onClick = {
                                    categoriaSeleccionada = categoria
                                    expandidoCategoria = false
                                    errorCategoria = null
                                }
                            )
                        }
                    }
                }

                // Descripción del producto
                Text(
                    text = "Descripción del producto",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { 
                        if (it.length <= 200) {
                            descripcion = it
                            errorDescripcion = null
                        } else {
                            errorDescripcion = "Máximo 200 caracteres"
                        }
                    },
                    placeholder = { Text("maximo 200 caracteres") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF669C4A),
                        errorBorderColor = Color.Red
                    ),
                    isError = errorDescripcion != null,
                    supportingText = { 
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            errorDescripcion?.let { Text(it, color = Color.Red) }
                            Text("${descripcion.length}/200", 
                                color = if (descripcion.length > 200) Color.Red else Color.Gray)
                        }
                    }
                )

                // Ubicación
                Text(
                    text = "Ubicación",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = ubicacion,
                    onValueChange = { 
                        if (it.length <= 30) {
                            ubicacion = it
                            errorUbicacion = null
                        } else {
                            errorUbicacion = "Máximo 30 caracteres"
                        }
                    },
                    placeholder = { Text("Lugar dentro del campus") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF669C4A),
                        errorBorderColor = Color.Red
                    ),
                    singleLine = true,
                    isError = errorUbicacion != null,
                    supportingText = { errorUbicacion?.let { Text(it, color = Color.Red) } }
                )

                // Precio
                Text(
                    text = "Precio",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { newValue ->
                        val regex = Regex("^\\d*\\.?\\d{0,2}$")
                        if (newValue.isEmpty() || regex.matches(newValue)) {
                            precio = newValue
                            errorPrecio = null
                        } else {
                            errorPrecio = "Formato inválido. Ejemplo: 99.99"
                        }
                    },
                    placeholder = { Text("Ingresa el precio") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF669C4A),
                        errorBorderColor = Color.Red
                    ),
                    singleLine = true,
                    isError = errorPrecio != null,
                    supportingText = { errorPrecio?.let { Text(it, color = Color.Red) } }
                )

                // Sección de imagen
                Text(
                    text = "Imagen del producto",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                if (imagenUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = imagenUri,
                            contentDescription = "Imagen seleccionada",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                
                OutlinedButton(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = greenColor
                    )
                ) {
                    Icon(
                        imageVector = if (imagenUri == null) Icons.Default.Add else Icons.Default.Edit,
                        contentDescription = null,
                        tint = greenColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (imagenUri == null) "Subir imagen" else "Elegir otra imagen",
                        color = greenColor
                    )
                }

                // Botón Publicar con padding extra al final
                Button(
                    onClick = { 
                        // Validar todos los campos antes de publicar
                        if (nombreProducto.isEmpty()) {
                            errorNombre = "Campo requerido"
                        }
                        if (categoriaSeleccionada.isEmpty()) {
                            errorCategoria = "Selecciona una categoría"
                        }
                        if (descripcion.isEmpty()) {
                            errorDescripcion = "Campo requerido"
                        }
                        if (ubicacion.isEmpty()) {
                            errorUbicacion = "Campo requerido"
                        }
                        if (precio.isEmpty()) {
                            errorPrecio = "Campo requerido"
                        }
                        if (imagenUri == null) {
                            // TODO: Mostrar un mensaje de error para la imagen
                        }
                        
                        // Si no hay errores, proceder con la publicación
                        if (errorNombre == null && errorCategoria == null && 
                            errorDescripcion == null && errorUbicacion == null && 
                            errorPrecio == null && imagenUri != null) {
                            // TODO: Implementar publicación
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = greenColor
                    )
                ) {
                    Text("Publicar")
                }
            }
        }
    }
} 