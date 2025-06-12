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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tianguisuni.ui.state.NewPublicationState
import com.example.tianguisuni.viewmodel.NewPublicationViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaPublicacionScreen(
    viewModel: NewPublicationViewModel,
    userId: String,
    onClose: () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val greenColor = Color(0xFF669C4A)
    val context = LocalContext.current
    
    // Estados
    val state by viewModel.state.collectAsState()
    val formState by viewModel.formState.collectAsState()
    
    // Inicializar ViewModel
    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }
    
    // Configurar colores de la barra de estado y navegación
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = greenColor,
            darkIcons = false
        )
    }

    // Efecto para manejar el estado de la publicación
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        when (state) {
            is NewPublicationState.Success -> {
                dialogMessage = "¡Publicación creada con éxito!"
                isError = false
                showDialog = true
            }
            is NewPublicationState.Error -> {
                dialogMessage = (state as NewPublicationState.Error).message
                isError = true
                showDialog = true
            }
            else -> {}
        }
    }

    when (val currentState = state) {
        is NewPublicationState.Success -> {
            AlertDialog(
                onDismissRequest = {
                    viewModel.resetForm()
                },
                title = { Text("Éxito") },
                text = { Text("¡Publicación creada con éxito!") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.resetForm()
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }
        is NewPublicationState.Error -> {
            AlertDialog(
                onDismissRequest = {
                    viewModel.resetForm()
                },
                title = { Text("Error") },
                text = { Text(currentState.message) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.resetForm()
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }
        else -> {}
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.updateImage(uri)
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
                    Box(modifier = Modifier.size(48.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Nombre del producto
            Text(
                text = "Nombre del producto",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            OutlinedTextField(
                value = formState.name,
                onValueChange = viewModel::updateName,
                placeholder = { Text("máximo 30 caracteres") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = greenColor,
                    errorBorderColor = Color.Red
                ),
                isError = formState.nameError != null,
                supportingText = { 
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        formState.nameError?.let { Text(it, color = Color.Red) }
                        Text("${formState.name.length}/30", 
                            color = if (formState.name.length > 30) Color.Red else Color.Gray)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categoría
            Text(
                text = "Categoría",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            val categories = listOf("Comida", "Bebida", "Ropa", "Dulces", "Regalos", "Otros")
            var expandedCategory by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it }
            ) {
                OutlinedTextField(
                    value = formState.category,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Selecciona una categoría") },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = greenColor,
                        errorBorderColor = Color.Red
                    ),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    isError = formState.categoryError != null,
                    supportingText = { formState.categoryError?.let { Text(it, color = Color.Red) } }
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = { 
                                viewModel.updateCategory(category)
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Descripción
            Text(
                text = "Descripción del producto",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            OutlinedTextField(
                value = formState.description,
                onValueChange = viewModel::updateDescription,
                placeholder = { Text("maximo 200 caracteres") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = greenColor,
                    errorBorderColor = Color.Red
                ),
                isError = formState.descriptionError != null,
                supportingText = { 
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        formState.descriptionError?.let { Text(it, color = Color.Red) }
                        Text("${formState.description.length}/200", 
                            color = if (formState.description.length > 200) Color.Red else Color.Gray)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ubicación
            Text(
                text = "Ubicación",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            OutlinedTextField(
                value = formState.location,
                onValueChange = viewModel::updateLocation,
                placeholder = { Text("máximo 30 caracteres") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = greenColor,
                    errorBorderColor = Color.Red
                ),
                isError = formState.locationError != null,
                supportingText = { 
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        formState.locationError?.let { Text(it, color = Color.Red) }
                        Text("${formState.location.length}/30", 
                            color = if (formState.location.length > 30) Color.Red else Color.Gray)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Precio
            Text(
                text = "Precio",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            OutlinedTextField(
                value = formState.price,
                onValueChange = viewModel::updatePrice,
                placeholder = { Text("Ejemplo: 99.99") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = greenColor,
                    errorBorderColor = Color.Red
                ),
                isError = formState.priceError != null,
                supportingText = { formState.priceError?.let { Text(it, color = Color.Red) } }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Imagen
            Text(
                text = "Imagen del producto",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (formState.imageUri != null) {
                    AsyncImage(
                        model = formState.imageUri,
                        contentDescription = "Imagen del producto",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    FloatingActionButton(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        containerColor = greenColor
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Cambiar imagen")
                    }
                } else {
                    IconButton(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Agregar imagen",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
            if (formState.imageError != null) {
                Text(
                    text = formState.imageError!!,
                    color = Color.Red,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Botón Publicar
            Button(
                onClick = { viewModel.createPublication(context, userId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = greenColor
                ),
                enabled = state !is NewPublicationState.Loading
            ) {
                if (state is NewPublicationState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Publicar")
                }
            }
        }
    }
} 