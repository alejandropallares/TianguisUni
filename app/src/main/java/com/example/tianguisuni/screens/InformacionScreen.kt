package com.example.tianguisuni.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformacionScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Información de la Aplicación") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Objetivo de la aplicación
            Text(
                text = "Objetivo de la Aplicación",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "TianguisUniversitario es una app que permite a los alumnos que venden dentro de la universidad publicar sus artículos de venta y difundir de manera más eficiente sus productos con la comunidad universitaria.",
                fontSize = 16.sp,
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            // Guía de navegación
            Text(
                text = "Guía de Navegación",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Pantalla Principal
            ListItem(
                headlineContent = { Text("Pantalla Principal") },
                supportingContent = { Text("Explora todas las publicaciones disponibles y filtra por categorías") },
                leadingContent = { Icon(Icons.Default.Home, contentDescription = null) }
            )

            // Mis Publicaciones
            ListItem(
                headlineContent = { Text("Mis Publicaciones") },
                supportingContent = { Text("Gestiona tus publicaciones, edita o elimina productos, y añade nuevos con el botón +") },
                leadingContent = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) }
            )

            // Nueva Publicación
            ListItem(
                headlineContent = { Text("Nueva Publicación") },
                supportingContent = { Text("Crea una nueva publicación con fotos, descripción, precio y detalles de contacto") },
                leadingContent = { Icon(Icons.Default.Add, contentDescription = null) }
            )

            // Perfil
            ListItem(
                headlineContent = { Text("Perfil") },
                supportingContent = { Text("Accede a tu información personal y gestiona tu cuenta") },
                leadingContent = { Icon(Icons.Default.Person, contentDescription = null) }
            )

            // Preferencias
            ListItem(
                headlineContent = { Text("Preferencias") },
                supportingContent = { Text("Personaliza la apariencia de las publicaciones ajustando los colores") },
                leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) }
            )

            // Funciones Especiales
            Text(
                text = "Funciones Especiales",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Compartir
            ListItem(
                headlineContent = { Text("Compartir Publicaciones") },
                supportingContent = { Text("Comparte cualquier publicación directamente a WhatsApp desde la vista de detalles") },
                leadingContent = { Icon(Icons.Default.Share, contentDescription = null) }
            )

            // Búsqueda
            ListItem(
                headlineContent = { Text("Filtros") },
                supportingContent = { Text("Utiliza los filtros para encontrar productos específicos") },
                leadingContent = { Icon(Icons.Default.Search, contentDescription = null) }
            )
        }
    }
} 