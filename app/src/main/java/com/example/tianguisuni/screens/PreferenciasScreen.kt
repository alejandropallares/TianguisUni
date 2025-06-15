package com.example.tianguisuni.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.tianguisuni.viewmodel.PreferenciasViewModel
import com.example.tianguisuni.viewmodel.PreferenciasViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenciasScreen(
    onNavigateBack: () -> Unit,
    preferenciasViewModel: PreferenciasViewModel = viewModel(
        factory = PreferenciasViewModelFactory(LocalContext.current)
    )
) {
    // Estado para el diálogo de confirmación
    var showConfirmationDialog by remember { mutableStateOf(false) }

    // Obtener el color guardado
    val colorPreferences by preferenciasViewModel.colorPreferences.collectAsState(initial = Triple(162, 208, 115))
    
    // Estado para los sliders
    var redValue by remember(colorPreferences.first) { mutableStateOf(colorPreferences.first.toFloat()) }
    var greenValue by remember(colorPreferences.second) { mutableStateOf(colorPreferences.second.toFloat()) }
    var blueValue by remember(colorPreferences.third) { mutableStateOf(colorPreferences.third.toFloat()) }

    val currentColor = Color(
        red = redValue.toInt(),
        green = greenValue.toInt(),
        blue = blueValue.toInt(),
        alpha = 255
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Preferencias",
                style = MaterialTheme.typography.titleLarge
            )
            // Empty box for alignment
            Box(modifier = Modifier.width(48.dp))
        }

        Text(
            text = "Color de Fondo de Tarjetas",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Color Sliders
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Red Slider
            Column {
                Text(
                    text = "Rojo: ${redValue.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Slider(
                    value = redValue,
                    onValueChange = { redValue = it },
                    valueRange = 0f..255f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Red,
                        activeTrackColor = Color.Red
                    )
                )
            }

            // Green Slider
            Column {
                Text(
                    text = "Verde: ${greenValue.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Slider(
                    value = greenValue,
                    onValueChange = { greenValue = it },
                    valueRange = 0f..255f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Green,
                        activeTrackColor = Color.Green
                    )
                )
            }

            // Blue Slider
            Column {
                Text(
                    text = "Azul: ${blueValue.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Slider(
                    value = blueValue,
                    onValueChange = { blueValue = it },
                    valueRange = 0f..255f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Blue,
                        activeTrackColor = Color.Blue
                    )
                )
            }

            // Color Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(currentColor)
                    .padding(16.dp)
            )

            // HEX Color Display
            Text(
                text = "HEX: #${String.format("%02X%02X%02X", 
                    redValue.toInt(), 
                    greenValue.toInt(), 
                    blueValue.toInt()
                )}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Start
            )

            // Save Button
            Button(
                onClick = { 
                    preferenciasViewModel.saveColorPreferences(
                        redValue.toInt(),
                        greenValue.toInt(),
                        blueValue.toInt()
                    )
                    showConfirmationDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Guardar")
            }
        }
    }

    // Confirmation Dialog
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { 
                showConfirmationDialog = false
                onNavigateBack()
            },
            title = { Text("Éxito") },
            text = { Text("El color se ha guardado correctamente") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showConfirmationDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
} 