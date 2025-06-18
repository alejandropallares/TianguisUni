package com.example.tianguisuni.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tianguisuni.viewmodel.AuthViewModel
import com.example.tianguisuni.viewmodel.AuthViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToPreferences: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory.provideFactory(
            context = LocalContext.current
        )
    )
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var hasShownLoginAlert by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    val currentUserId = authViewModel.getCurrentUserId()
    val currentUsername = authViewModel.getCurrentUsername()
    val isLoggedIn = currentUserId != null
    
    val loginResult by authViewModel.loginResult.collectAsState()

    // Observar el resultado del login
    LaunchedEffect(loginResult) {
        loginResult?.let { result ->
            isLoading = false
            if (!hasShownLoginAlert) {
                result.onSuccess { response ->
                    errorMessage = "Inicio de sesión exitoso"
                    showErrorDialog = true
                    hasShownLoginAlert = true
                }.onFailure { exception ->
                    errorMessage = exception.message ?: "Error al iniciar sesión"
                    showErrorDialog = true
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (isLoggedIn) {
            // Pantalla de perfil del usuario
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Encabezado con título y botón de preferencias
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Espaciador para mantener el título centrado
                    Spacer(modifier = Modifier.width(48.dp))
                    Text(
                        text = "Mi cuenta",
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = onNavigateToPreferences) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Preferencias",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Información del usuario
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "Nombre de Usuario",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = currentUsername ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Botón de cerrar sesión
                TextButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = "Cerrar Sesión",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            // Pantalla de inicio de sesión
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 32.dp)
                )

                // Campos de inicio de sesión
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Text(
                        text = "Nombre de usuario",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { if (it.length <= 20) username = it },
                        modifier = Modifier.fillMaxWidth(),
                        isError = username.isNotEmpty() && !username.matches(Regex("^[a-zA-Z0-9._]{0,20}$")),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (username.isNotEmpty() && !username.matches(Regex("^[a-zA-Z0-9._]{0,20}$"))) Color.Red else MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = if (username.isNotEmpty() && !username.matches(Regex("^[a-zA-Z0-9._]{0,20}$"))) Color.Red else MaterialTheme.colorScheme.outline
                        )
                    )

                    Text(
                        text = "Contraseña",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Close else Icons.Default.Info,
                                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                                )
                            }
                        }
                    )

                    // Botón de inicio de sesión
                    Button(
                        onClick = {
                            isLoading = true
                            hasShownLoginAlert = false
                            authViewModel.login(username, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        enabled = !isLoading && username.isNotEmpty() && password.isNotEmpty()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Iniciar Sesión")
                        }
                    }

                    // Enlace para registrarse
                    TextButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("¿No tienes cuenta? Regístrate")
                    }
                }
            }
        }
    }

    // Diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { 
                showErrorDialog = false
                if (errorMessage == "Inicio de sesión exitoso") {
                    onLoginClick()
                }
            },
            title = { Text(if (errorMessage == "Inicio de sesión exitoso") "Éxito" else "Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showErrorDialog = false
                        if (errorMessage == "Inicio de sesión exitoso") {
                            onLoginClick()
                        }
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Diálogo de cerrar sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.logout()
                        hasShownLoginAlert = false
                        showLogoutDialog = false
                    }
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("No")
                }
            }
        )
    }
} 