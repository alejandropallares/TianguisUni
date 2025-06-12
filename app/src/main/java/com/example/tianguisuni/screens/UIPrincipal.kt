package com.example.tianguisuni.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tianguisuni.viewmodel.AuthViewModel
import com.example.tianguisuni.viewmodel.NewPublicationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UIPrincipal() {
    var selectedTab by remember { mutableStateOf(0) }
    var showNuevaPublicacion by remember { mutableStateOf(false) }
    var showRegistro by remember { mutableStateOf(false) }
    val newPublicationViewModel: NewPublicationViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    
    val currentUserId = authViewModel.getCurrentUserId()

    when {
        showNuevaPublicacion -> {
            if (currentUserId != null) {
                NuevaPublicacionScreen(
                    viewModel = newPublicationViewModel,
                    userId = currentUserId,
                    onClose = { showNuevaPublicacion = false }
                )
            } else {
                // Si no hay usuario logueado, mostrar pantalla de registro
                showRegistro = true
                showNuevaPublicacion = false
            }
        }
        showRegistro -> {
            RegistroScreen(
                onNavigateBack = { showRegistro = false },
                onRegistroExitoso = { showRegistro = false }
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
                                if (currentUserId != null) {
                                    showNuevaPublicacion = true
                                } else {
                                    showRegistro = true
                                }
                            }
                        )
                        1 -> MisPublicacionesScreen(
                            onNavigateToNuevaPublicacion = { 
                                if (currentUserId != null) {
                                    showNuevaPublicacion = true
                                } else {
                                    showRegistro = true
                                }
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