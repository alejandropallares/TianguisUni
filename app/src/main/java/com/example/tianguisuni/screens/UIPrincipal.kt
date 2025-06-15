package com.example.tianguisuni.screens

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tianguisuni.viewmodel.AuthViewModel
import com.example.tianguisuni.viewmodel.NewPublicationViewModel
import com.example.tianguisuni.viewmodel.PublicacionViewModel
import com.example.tianguisuni.viewmodel.PublicacionViewModelFactory
import android.app.Application
import com.example.tianguisuni.data.entities.Publicacion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UIPrincipal() {
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    var showNuevaPublicacion by rememberSaveable { mutableStateOf(false) }
    var showRegistro by rememberSaveable { mutableStateOf(false) }
    var publicacionToEdit: String? by rememberSaveable { mutableStateOf(null) }
    var selectedPublicacion: Publicacion? by rememberSaveable { mutableStateOf(null) }
    
    val context = LocalContext.current
    val newPublicationViewModel: NewPublicationViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val publicacionViewModel: PublicacionViewModel = viewModel(
        factory = PublicacionViewModelFactory(context.applicationContext as Application)
    )

    // Observar las publicaciones para encontrar la que se está editando
    val publicaciones by publicacionViewModel.publicaciones.collectAsState()
    val publicacionEnEdicion = publicacionToEdit?.let { uuid ->
        publicaciones.find { it.uuid == uuid }
    }

    when {
        selectedPublicacion != null -> {
            DetallesPublicacionScreen(
                publicacion = selectedPublicacion!!,
                onNavigateBack = { selectedPublicacion = null },
                onShare = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, """
                            ¡Mira este producto en Tianguis Universitario!
                            
                            ${selectedPublicacion!!.nombre_producto}
                            Precio: $${String.format("%.2f", selectedPublicacion!!.precio_producto)}
                            Categoría: ${selectedPublicacion!!.categoria_producto}
                            Ubicación: ${selectedPublicacion!!.ubicacion_producto}
                            Lo vende: ${selectedPublicacion!!.nombre_pila}
                            
                            Descripción: ${selectedPublicacion!!.descripcion_producto}
                        """.trimIndent())
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Compartir publicación"))
                }
            )
        }
        publicacionEnEdicion != null -> {
            EditarPublicacionScreen(
                publicacion = publicacionEnEdicion,
                viewModel = publicacionViewModel,
                onClose = { publicacionToEdit = null }
            )
        }
        showNuevaPublicacion -> {
            val currentUserId = authViewModel.getCurrentUserId()
            if (currentUserId != null) {
                NuevaPublicacionScreen(
                    viewModel = newPublicationViewModel,
                    userId = currentUserId,
                    onClose = { showNuevaPublicacion = false }
                )
            } else {
                showRegistro = true
                showNuevaPublicacion = false
            }
        }
        showRegistro -> {
            RegistroScreen(
                onNavigateBack = { showRegistro = false },
                onRegistroExitoso = {
                    showRegistro = false
                    if (authViewModel.getCurrentUserId() != null) {
                        showNuevaPublicacion = true
                    }
                }
            )
        }
        else -> {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                            label = { Text("Inicio") },
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.List, contentDescription = "Mis Publicaciones") },
                            label = { Text("Mis Publicaciones") },
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                            label = { Text("Perfil") },
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 }
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (selectedTab) {
                        0 -> HomeScreen(
                            onNavigateToNuevaPublicacion = { 
                                val currentUserId = authViewModel.getCurrentUserId()
                                if (currentUserId != null) {
                                    showNuevaPublicacion = true
                                } else {
                                    showRegistro = true
                                }
                            },
                            onNavigateToDetalles = { publicacion ->
                                selectedPublicacion = publicacion
                            }
                        )
                        1 -> MisPublicacionesScreen(
                            viewModel = publicacionViewModel,
                            userId = authViewModel.getCurrentUserId(),
                            onNavigateToNuevaPublicacion = { 
                                val currentUserId = authViewModel.getCurrentUserId()
                                if (currentUserId != null) {
                                    showNuevaPublicacion = true
                                } else {
                                    showRegistro = true
                                }
                            },
                            onNavigateToEditarPublicacion = { publicacion ->
                                publicacionToEdit = publicacion.uuid
                            }
                        )
                        2 -> PerfilScreen(
                            onNavigateToRegister = { showRegistro = true }
                        )
                    }
                }
            }
        }
    }
} 