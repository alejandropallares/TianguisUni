package com.example.tianguisuni.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.unit.sp
import com.example.tianguisuni.data.entities.Publicacion
import com.example.tianguisuni.viewmodel.PublicacionViewModel
import android.util.Base64
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.runtime.collectAsState
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPublicacionesScreen(
    viewModel: PublicacionViewModel? = null,
    userId: String? = null,
    onNavigateToNuevaPublicacion: () -> Unit
) {
    Log.d("MisPublicacionesScreen", "Renderizando pantalla. ViewModel: ${viewModel != null}, UserId: $userId")

    // Iniciar observación de publicaciones cuando se proporciona un userId
    LaunchedEffect(userId) {
        if (viewModel != null && userId != null) {
            Log.d("MisPublicacionesScreen", "Iniciando observación de publicaciones para usuario: $userId")
            viewModel.startObservingPublicaciones(userId)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Publicaciones") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNuevaPublicacion,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Publicación")
            }
        }
    ) { paddingValues ->
        if (viewModel == null || userId == null) {
            Log.d("MisPublicacionesScreen", "Usuario no autenticado")
            // Usuario no autenticado
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Inicia sesión para ver tus publicaciones",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            // Usuario autenticado - Mostrar publicaciones
            val publicaciones by viewModel.publicaciones.collectAsState()
            Log.d("MisPublicacionesScreen", "Publicaciones recibidas: ${publicaciones.size}")

            if (publicaciones.isEmpty()) {
                Log.d("MisPublicacionesScreen", "No hay publicaciones para mostrar")
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No tienes publicaciones",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                Log.d("MisPublicacionesScreen", "Mostrando ${publicaciones.size} publicaciones")
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(publicaciones) { publicacion ->
                        Log.d("MisPublicacionesScreen", "Renderizando publicación: ${publicacion.nombre_producto}")
                        PublicacionCard(publicacion = publicacion)
                    }
                }
            }
        }
    }
}

@Composable
fun PublicacionCard(publicacion: Publicacion) {
    Log.d("PublicacionCard", "Renderizando card para: ${publicacion.nombre_producto}")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            val imageBitmap = try {
                val imageBytes = Base64.decode(publicacion.imagen_producto, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)?.asImageBitmap()
            } catch (e: Exception) { 
                Log.e("PublicacionCard", "Error al decodificar imagen", e)
                null 
            }

            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Imagen del producto",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = publicacion.nombre_producto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Categoría: ${publicacion.categoria_producto}",
                    color = Color(0xFF669C4A),
                    fontSize = 14.sp
                )
            }

            // Botones de acción
            Column {
                IconButton(
                    onClick = { /* TODO: Implementar edición */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF669C4A)
                    )
                }
                IconButton(
                    onClick = { /* TODO: Implementar borrado */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Borrar",
                        tint = Color(0xFF669C4A)
                    )
                }
            }
        }
    }
} 