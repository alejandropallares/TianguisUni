package com.example.tianguisuni.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tianguisuni.data.entities.Usuario
import com.example.tianguisuni.viewmodel.AuthViewModel
import com.example.tianguisuni.viewmodel.AuthViewModelFactory
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onNavigateBack: () -> Unit,
    onRegistroExitoso: () -> Unit,
    authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory.provideFactory(
            context = androidx.compose.ui.platform.LocalContext.current
        )
    )
) {
    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessages by remember { mutableStateOf(listOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }

    val registerResult by authViewModel.registerResult.observeAsState()

    // Observar el resultado del registro
    LaunchedEffect(registerResult) {
        registerResult?.let { result ->
            isLoading = false
            result.onSuccess {
                onRegistroExitoso()
            }.onFailure { exception ->
                errorMessages = listOf(exception.message ?: "Error al registrar usuario")
                showErrorDialog = true
            }
        }
    }

    // Validaciones
    val isUsernameValid = username.matches(Regex("^[a-zA-Z0-9._]{0,20}$"))
    val isNameValid = name.matches(Regex("^[a-zA-Z]{0,20}$"))
    val isPasswordValid = password.matches(Regex("^(?=.*[A-Z])(?=.*[0-9]).{8,}$"))
    val doPasswordsMatch = password == confirmPassword

    fun validateForm(): Boolean {
        val errors = mutableListOf<String>()
        
        if (username.isEmpty()) {
            errors.add("El nombre de usuario no puede estar vacío")
        } else if (!isUsernameValid) {
            errors.add("El nombre de usuario debe tener máximo 20 caracteres, sin espacios y solo puede contener letras, números, punto y guion bajo")
        }
        
        if (name.isEmpty()) {
            errors.add("El nombre no puede estar vacío")
        } else if (!isNameValid) {
            errors.add("El nombre debe tener máximo 20 caracteres y solo puede contener letras")
        }
        
        if (password.isEmpty()) {
            errors.add("La contraseña no puede estar vacía")
        } else if (!isPasswordValid) {
            errors.add("La contraseña debe tener mínimo 8 caracteres, 1 mayúscula y 1 número")
        }
        
        if (confirmPassword.isEmpty() || !doPasswordsMatch) {
            errors.add("Las contraseñas no coinciden")
        }

        errorMessages = errors
        return errors.isEmpty()
    }

    fun handleRegistro() {
        if (validateForm()) {
            isLoading = true
            authViewModel.register(
                nombre = username,
                nombrePila = name,
                password = password
            )
        } else {
            showErrorDialog = true
        }
    }

    if (showErrorDialog && errorMessages.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Errores en el formulario") },
            text = {
                Column {
                    errorMessages.forEach { error ->
                        Text("• $error")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("Aceptar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Encabezado con título y botón de cerrar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 42.dp)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Registro de usuario",
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
            // Campo de usuario
            Column {
                Text(
                    text = "Nombre de usuario",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Máximo 20 caracteres, sin espacios",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { newValue ->
                        if (newValue.matches(Regex("^[a-zA-Z0-9._]{0,20}$"))) {
                            username = newValue
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe un nombre de usuario original") },
                    singleLine = true,
                    enabled = !isLoading
                )
            }

            // Campo de nombre
            Column {
                Text(
                    text = "Nombre",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { newValue ->
                        if (newValue.matches(Regex("^[a-zA-Z]{0,20}$"))) {
                            name = newValue
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe tu nombre de pila") },
                    singleLine = true,
                    enabled = !isLoading
                )
            }

            // Campo de contraseña
            Column {
                Text(
                    text = "Contraseña",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Mínimo 8 caracteres, 1 mayúscula y 1 número",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe tu contraseña") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    enabled = !isLoading,
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            }

            // Campo de confirmar contraseña
            Column {
                Text(
                    text = "Confirmar contraseña",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Confirma tu contraseña") },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    enabled = !isLoading,
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            }

            // Botón de Registrar
            Button(
                onClick = { handleRegistro() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Registrar")
                }
            }
        }
    }
} 