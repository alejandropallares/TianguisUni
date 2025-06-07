package com.example.tianguisuni.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaPublicacionScreen(
    onClose: () -> Unit
) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Barra superior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
            Text(
                text = "Nueva Publicación",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
            // Espacio para mantener el título centrado
            Box(modifier = Modifier.size(48.dp))
        }

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

            // Subir imagen
            if (imagenUri != null) {
                AsyncImage(
                    model = imagenUri,
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
            
            OutlinedButton(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF669C4A)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color(0xFF669C4A)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Subir imagen", color = Color(0xFF669C4A))
            }

            // Botón Publicar
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
                    containerColor = Color(0xFF669C4A)
                )
            ) {
                Text("Publicar")
            }
        }
    }
} 