package com.example.tianguisuni.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onNavigateBack: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessages by remember { mutableStateOf(listOf<String>()) }

    // Validaciones
    val isUsernameValid = username.matches(Regex("^[a-zA-Z0-9._]{0,20}$"))
    val isNameValid = name.matches(Regex("^[a-zA-Z]{0,20}$"))
    val isPasswordValid = password.matches(Regex("^(?=.*[A-Z])(?=.*[0-9]).{8,}$"))
    val doPasswordsMatch = password == confirmPassword

    // Mensajes de error para cada campo
    val usernameError = if (username.isNotEmpty() && !isUsernameValid) {
        "El nombre de usuario debe tener máximo 20 caracteres, sin espacios y solo puede contener letras, números, punto y guion bajo"
    } else null

    val nameError = if (name.isNotEmpty() && !isNameValid) {
        "El nombre debe tener máximo 20 caracteres y solo puede contener letras"
    } else null

    val passwordError = if (password.isNotEmpty() && !isPasswordValid) {
        "La contraseña debe tener mínimo 8 caracteres, 1 mayúscula y 1 número"
    } else null

    val confirmPasswordError = if (confirmPassword.isNotEmpty() && !doPasswordsMatch) {
        "Las contraseñas no coinciden"
    } else null

    fun validateForm(): Boolean {
        val errors = mutableListOf<String>()
        
        if (username.isEmpty()) {
            errors.add("El nombre de usuario no puede estar vacío")
        }
        
        if (name.isEmpty()) {
            errors.add("El nombre no puede estar vacío")
        }
        
        if (password.isEmpty()) {
            errors.add("La contraseña no puede estar vacía")
        }
        
        if (confirmPassword.isEmpty() || !doPasswordsMatch) {
            errors.add("Las contraseñas no coinciden")
        }

        errorMessages = errors
        return errors.isEmpty()
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                        // Solo permite letras, números, punto y guion bajo, máximo 20 caracteres
                        if (newValue.matches(Regex("^[a-zA-Z0-9._]{0,20}$"))) {
                            username = newValue
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe un nombre de usuario original") },
                    singleLine = true
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
                        // Solo permite letras, máximo 20 caracteres
                        if (newValue.matches(Regex("^[a-zA-Z]{0,20}$"))) {
                            name = newValue
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe tu nombre de pila") },
                    singleLine = true
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
                    onValueChange = { newValue ->
                        // No hay restricción de caracteres para la contraseña
                        password = newValue
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe tu contraseña") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
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
                    onValueChange = { newValue ->
                        // No hay restricción de caracteres para la confirmación de contraseña
                        confirmPassword = newValue
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe tu contraseña") },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
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
                onClick = {
                    if (!validateForm()) {
                        showErrorDialog = true
                    } else {
                        // TODO: Implementar registro
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            ) {
                Text(
                    text = "Registrar",
                    color = Color.Black
                )
            }
        }
    }
} 