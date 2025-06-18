package com.example.tianguisuni.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tianguisuni.viewmodel.PublicacionesViewModel
import com.example.tianguisuni.viewmodel.PublicacionesViewModelFactory
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.tianguisuni.data.entities.Publicacion
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    onNavigateToNuevaPublicacion: () -> Unit,
    onNavigateToDetalles: (Publicacion) -> Unit,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    val categories = listOf("Todo", "Comida", "Bebida", "Ropa", "Dulces", "Regalos", "Otros")
    var selectedCategory by remember { mutableStateOf("Todo") }
    val context = LocalContext.current
    
    val viewModel: PublicacionesViewModel = viewModel(
        factory = PublicacionesViewModelFactory(context.applicationContext as android.app.Application)
    )
    
    val publicaciones by viewModel.publicaciones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val error by viewModel.error.collectAsState()

    // Efecto para reiniciar el estado cuando se vuelve a mostrar la pantalla
    LaunchedEffect(Unit) {
        selectedCategory = "Todo"
        viewModel.filtrarPorCategoria("Todo")
    }

    // Pull to refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh() }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título
        Text(
            text = "Tianguis Universitario",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Categorías
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { 
                        selectedCategory = category
                        viewModel.filtrarPorCategoria(category)
                    },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        // Estado de carga y error
        if (isLoading && publicaciones.isEmpty() && selectedCategory == "Todo") {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else if (publicaciones.isEmpty()) {
            Text(
                text = if (selectedCategory == "Todo") 
                    "No hay publicaciones disponibles"
                else 
                    "No hay publicaciones de la categoría ${selectedCategory.lowercase()}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Lista de publicaciones con pull to refresh
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            if (publicaciones.isEmpty() && !isLoading && !isRefreshing) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (selectedCategory == "Todo") 
                            "No hay publicaciones disponibles" 
                        else 
                            "No hay publicaciones de la categoría ${selectedCategory.lowercase()}",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(publicaciones) { publicacionConUsuario ->
                        val publicacion = publicacionConUsuario.publicacion
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { onNavigateToDetalles(publicacion) },
                            colors = CardDefaults.cardColors(
                                containerColor = accentColor
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Imagen del producto
                                val imageBitmap = remember(publicacion.imagen_producto) {
                                    decodeBase64ToBitmap(publicacion.imagen_producto)
                                }
                                
                                if (imageBitmap != null) {
                                    Image(
                                        bitmap = imageBitmap.asImageBitmap(),
                                        contentDescription = "Imagen de ${publicacion.nombre_producto}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Error al cargar la imagen")
                                    }
                                }

                                // Información del producto
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Categoría
                                    Text(
                                        text = publicacion.categoria_producto,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    // Nombre del producto
                                    Text(
                                        text = publicacion.nombre_producto,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )

                                    // Vendedor
                                    Text(
                                        text = "De: ${publicacionConUsuario.nombreUsuario}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    // Precio
                                    Text(
                                        text = "$${String.format("%.2f", publicacion.precio_producto)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Pull to refresh indicator solo se muestra cuando hay publicaciones o durante la carga/refresco
                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

private fun decodeBase64ToBitmap(base64String: String): android.graphics.Bitmap? {
    return try {
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } catch (e: Exception) {
        null
    }
} 