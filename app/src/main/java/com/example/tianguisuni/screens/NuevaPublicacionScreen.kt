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
    onClose: () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val greenColor = Color(0xFF669C4A)
    val context = LocalContext.current
    
    // Estados
    val state by viewModel.state.collectAsState()
    val formState by viewModel.formState.collectAsState()
    
    // Configurar colores de la barra de estado y navegación
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = greenColor,
            darkIcons = false
        )
    }

    // Efecto para manejar el estado de la publicación
    LaunchedEffect(state) {
        when (state) {
            is NewPublicationState.Success -> {
                // TODO: Mostrar mensaje de éxito
                onClose()
            }
            is NewPublicationState.Error -> {
                // TODO: Mostrar mensaje de error
            }
            else -> {}
        }
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
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(bottom = 80.dp)
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
                    value = formState.name,
                    onValueChange = viewModel::updateName,
                    placeholder = { Text("Escribe el nombre de tu producto") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = greenColor,
                        errorBorderColor = Color.Red
                    ),
                    singleLine = true,
                    isError = formState.nameError != null,
                    supportingText = { formState.nameError?.let { Text(it, color = Color.Red) } }
                )

                // Categoría
                Text(
                    text = "Categoria",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                var expandidoCategoria by remember { mutableStateOf(false) }
                val categorias = listOf("Dulces", "Comida", "Bebidas", "Ropa", "Regalos", "Otros")
                
                ExposedDropdownMenuBox(
                    expanded = expandidoCategoria,
                    onExpandedChange = { expandidoCategoria = it }
                ) {
                    OutlinedTextField(
                        value = formState.category,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Selecciona categoria") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = greenColor,
                            errorBorderColor = Color.Red
                        ),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoCategoria) },
                        isError = formState.categoryError != null,
                        supportingText = { formState.categoryError?.let { Text(it, color = Color.Red) } }
                    )
                    ExposedDropdownMenu(
                        expanded = expandidoCategoria,
                        onDismissRequest = { expandidoCategoria = false }
                    ) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria) },
                                onClick = {
                                    viewModel.updateCategory(categoria)
                                    expandidoCategoria = false
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

                // Ubicación
                Text(
                    text = "Ubicación",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = formState.location,
                    onValueChange = viewModel::updateLocation,
                    placeholder = { Text("Lugar dentro del campus") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = greenColor,
                        errorBorderColor = Color.Red
                    ),
                    singleLine = true,
                    isError = formState.locationError != null,
                    supportingText = { formState.locationError?.let { Text(it, color = Color.Red) } }
                )

                // Precio
                Text(
                    text = "Precio",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = formState.price,
                    onValueChange = viewModel::updatePrice,
                    placeholder = { Text("Ingresa el precio") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = greenColor,
                        errorBorderColor = Color.Red
                    ),
                    singleLine = true,
                    isError = formState.priceError != null,
                    supportingText = { formState.priceError?.let { Text(it, color = Color.Red) } }
                )

                // Sección de imagen
                Text(
                    text = "Imagen del producto",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                if (formState.imageUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = Uri.parse(formState.imageUri),
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
                        imageVector = if (formState.imageUri == null) Icons.Default.Add else Icons.Default.Edit,
                        contentDescription = null,
                        tint = greenColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (formState.imageUri == null) "Subir imagen" else "Elegir otra imagen",
                        color = greenColor
                    )
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
                    onClick = viewModel::createPublication,
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
} 