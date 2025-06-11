package com.example.tianguisuni.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
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
    
    val sessionState by authViewModel.sessionState.collectAsState()

    // Observar el resultado del login
    LaunchedEffect(Unit) {
        authViewModel.loginResult.collect { result ->
            isLoading = false
            result.onSuccess { response ->
                errorMessage = "Inicio de sesión exitoso"
                showErrorDialog = true
            }.onFailure { exception ->
                errorMessage = exception.message ?: "Error al iniciar sesión"
                showErrorDialog = true
            }
        }
    }

    if (sessionState.isLoggedIn) {
        // Pantalla de perfil del usuario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Encabezado con título y botón de regresar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO: Implementar navegación hacia atrás */ }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Mi cuenta",
                    style = MaterialTheme.typography.titleLarge
                )
                // Espaciador para mantener el título centrado
                Spacer(modifier = Modifier.width(48.dp))
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
                    text = sessionState.currentUser ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Botón de cerrar sesión
            TextButton(
                onClick = {
                    authViewModel.logout()
                },
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
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showErrorDialog = false
                },
                title = { Text(if (errorMessage.contains("exitoso")) "Éxito" else "Error") },
                text = { Text(errorMessage) },
                confirmButton = {
                    TextButton(
                        onClick = { 
                            showErrorDialog = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado con título y botón de cerrar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO: Implementar navegación hacia atrás */ }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Inicio de Sesión",
                    style = MaterialTheme.typography.titleLarge
                )
                // Espaciador para mantener el título centrado
                Spacer(modifier = Modifier.width(48.dp))
            }

            // Formulario
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    isError = password.isNotEmpty() && password.length < 8,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (password.isNotEmpty() && password.length < 8) Color.Red else MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = if (password.isNotEmpty() && password.length < 8) Color.Red else MaterialTheme.colorScheme.outline
                    )
                )

                // Botón de Entrar
                Button(
                    onClick = { 
                        if (username.isEmpty() || password.isEmpty()) {
                            errorMessage = "Por favor ingresa usuario y contraseña"
                            showErrorDialog = true
                            return@Button
                        }
                        
                        if (!username.matches(Regex("^[a-zA-Z0-9._]{0,20}$"))) {
                            errorMessage = "Nombre de usuario inválido"
                            showErrorDialog = true
                            return@Button
                        }
                        
                        if (password.length < 8) {
                            errorMessage = "La contraseña debe tener al menos 8 caracteres"
                            showErrorDialog = true
                            return@Button
                        }
                        
                        isLoading = true
                        authViewModel.login(username, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    enabled = !isLoading && username.isNotEmpty() && password.isNotEmpty()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Entrar")
                    }
                }

                // Texto y enlace de registro
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "¿No tienes una cuenta? ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(
                        onClick = onNavigateToRegister,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Regístrate",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
} 