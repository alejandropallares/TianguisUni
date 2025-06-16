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
    var isLoading by remember { mutableStateOf(false) }

    // Estados para los errores de cada campo
    var usernameError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val registerResult by authViewModel.registerResult.observeAsState()

    // Observar el resultado del registro
    LaunchedEffect(registerResult) {
        registerResult?.let { result ->
            isLoading = false
            result.onSuccess {
                onRegistroExitoso()
            }.onFailure { exception ->
                // Mostrar error en un Snackbar o AlertDialog según sea necesario
            }
        }
    }

    // Funciones de validación en tiempo real
    fun validateUsername(value: String): String? {
        return when {
            value.isEmpty() -> "El nombre de usuario no puede estar vacío"
            !value.matches(Regex("^[a-zA-Z0-9._]{0,20}$")) -> 
                "El nombre de usuario debe tener máximo 20 caracteres, sin espacios y solo puede contener letras, números, punto y guion bajo"
            else -> null
        }
    }

    fun validateName(value: String): String? {
        return when {
            value.isEmpty() -> "El nombre no puede estar vacío"
            !value.matches(Regex("^[a-zA-Z]{0,20}$")) ->
                "El nombre debe tener máximo 20 caracteres y solo puede contener letras"
            else -> null
        }
    }

    fun validatePassword(value: String): String? {
        return when {
            value.isEmpty() -> "La contraseña no puede estar vacía"
            !value.matches(Regex("^(?=.*[A-Z])(?=.*[0-9]).{8,}$")) ->
                "La contraseña debe tener mínimo 8 caracteres, 1 mayúscula y 1 número"
            else -> null
        }
    }

    fun validateConfirmPassword(value: String, password: String): String? {
        return when {
            value.isEmpty() -> "Debes confirmar la contraseña"
            value != password -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    fun handleRegistro() {
        val isValid = usernameError == null && 
                     nameError == null && 
                     passwordError == null && 
                     confirmPasswordError == null &&
                     username.isNotEmpty() &&
                     name.isNotEmpty() &&
                     password.isNotEmpty() &&
                     confirmPassword.isNotEmpty()

        if (isValid) {
            isLoading = true
            authViewModel.register(
                nombre = username,
                nombrePila = name,
                password = password
            )
        }
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
                        // Solo permitir letras, números, punto y guion bajo
                        val filteredValue = newValue.filter { char ->
                            char.isLetterOrDigit() || char == '.' || char == '_'
                        }
                        if (filteredValue.length <= 20) {
                            username = filteredValue
                            usernameError = validateUsername(filteredValue)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe un nombre de usuario original") },
                    singleLine = true,
                    enabled = !isLoading,
                    isError = usernameError != null
                )
                if (usernameError != null) {
                    Text(
                        text = usernameError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
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
                        // Solo permitir letras
                        val filteredValue = newValue.filter { it.isLetter() }
                        if (filteredValue.length <= 20) {
                            name = filteredValue
                            nameError = validateName(filteredValue)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe tu nombre de pila") },
                    singleLine = true,
                    enabled = !isLoading,
                    isError = nameError != null
                )
                if (nameError != null) {
                    Text(
                        text = nameError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
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
                        // Permitir cualquier caracter para la contraseña pero limitar longitud
                        if (newValue.length <= 50) { // Agregamos un límite razonable
                            password = newValue
                            passwordError = validatePassword(newValue)
                            confirmPasswordError = validateConfirmPassword(confirmPassword, newValue)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe tu contraseña") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    enabled = !isLoading,
                    isError = passwordError != null,
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
                if (passwordError != null) {
                    Text(
                        text = passwordError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
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
                        confirmPassword = newValue
                        confirmPasswordError = validateConfirmPassword(newValue, password)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Confirma tu contraseña") },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    enabled = !isLoading,
                    isError = confirmPasswordError != null,
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
                if (confirmPasswordError != null) {
                    Text(
                        text = confirmPasswordError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Botón de Registrar
            Button(
                onClick = { handleRegistro() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && 
                         usernameError == null && 
                         nameError == null && 
                         passwordError == null && 
                         confirmPasswordError == null &&
                         username.isNotEmpty() &&
                         name.isNotEmpty() &&
                         password.isNotEmpty() &&
                         confirmPassword.isNotEmpty()
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