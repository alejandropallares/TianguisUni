package com.example.tianguisuni.screens

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tianguisuni.data.entities.Publicacion
import com.example.tianguisuni.viewmodel.PublicacionViewModel
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPublicacionScreen(
    publicacion: Publicacion,
    viewModel: PublicacionViewModel,
    onClose: () -> Unit
) {
    var nombre by remember { mutableStateOf(publicacion.nombre_producto) }
    var categoria by remember { mutableStateOf(publicacion.categoria_producto) }
    var descripcion by remember { mutableStateOf(publicacion.descripcion_producto) }
    var ubicacion by remember { mutableStateOf(publicacion.ubicacion_producto) }
    var precio by remember { mutableStateOf(publicacion.precio_producto.toString()) }
    var imagenBase64 by remember { mutableStateOf(publicacion.imagen_producto) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            // Convertir Uri a Base64
            context.contentResolver.openInputStream(selectedUri)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                imagenBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(50.dp)
    ) {
        // Barra superior con título y botón cerrar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón X para cerrar
            IconButton(
                onClick = { showDiscardDialog = true }
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }

            Text(
                text = "Editar Publicación",
                style = MaterialTheme.typography.headlineSmall
            )

            // Espacio vacío para mantener el título centrado
            Box(modifier = Modifier.size(48.dp))
        }

        // Imagen del producto con botón de edición
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 16.dp)
        ) {
            // Mostrar imagen
            val imageBitmap = try {
                val imageBytes = Base64.decode(imagenBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)?.asImageBitmap()
            } catch (e: Exception) { 
                null 
            }

            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Imagen del producto",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                )
            }

            // Botón de edición sobre la imagen
            FloatingActionButton(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                containerColor = Color(0xFF669C4A)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar imagen",
                    tint = Color.White
                )
            }
        }

        // Campos de edición
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del producto") },
            placeholder = { Text("Escribe el nombre de tu producto") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
            placeholder = { Text("Selecciona categoría") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción del producto") },
            placeholder = { Text("máximo 200 caracteres") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(bottom = 16.dp),
            maxLines = 5
        )

        OutlinedTextField(
            value = ubicacion,
            onValueChange = { ubicacion = it },
            label = { Text("Ubicación") },
            placeholder = { Text("Lugar dentro del campus") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Botón Guardar
        Button(
            onClick = { showConfirmDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF669C4A)
            )
        ) {
            Text("Guardar")
        }
    }

    // Diálogo de confirmación para guardar
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar cambios") },
            text = { Text("¿Estás seguro de que deseas guardar los cambios?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val publicacionActualizada = publicacion.copy(
                            nombre_producto = nombre,
                            categoria_producto = categoria,
                            descripcion_producto = descripcion,
                            ubicacion_producto = ubicacion,
                            precio_producto = precio.toDoubleOrNull() ?: 0.0,
                            imagen_producto = imagenBase64
                        )
                        viewModel.actualizarPublicacion(publicacionActualizada)
                        showConfirmDialog = false
                        onClose()
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de confirmación para descartar cambios
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Descartar cambios") },
            text = { Text("¿Estás seguro de que deseas descartar los cambios?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        onClose()
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
} 