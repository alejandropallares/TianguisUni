package com.example.tianguisuni.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tianguisuni.data.database.DatabaseProvider
import com.example.tianguisuni.data.entities.Usuario
import com.example.tianguisuni.data.network.RetrofitClient
import com.example.tianguisuni.data.network.models.LoginRequest
import com.example.tianguisuni.data.network.models.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed class RegistroError : Exception() {
    object UsuarioExistente : RegistroError() {
        override val message: String = "El nombre de usuario ya está en uso. Por favor, elige otro nombre de usuario."
    }
    class ErrorGenerico(override val message: String) : RegistroError()
}

class AuthViewModel : ViewModel() {
    private val api = RetrofitClient.apiService
    private var databaseProvider: DatabaseProvider? = null
    
    private val _loginResult = MutableStateFlow<Result<LoginResponse>?>(null)
    val loginResult: StateFlow<Result<LoginResponse>?> = _loginResult

    private val _registerResult = MutableStateFlow<Result<Unit>?>(null)
    val registerResult: StateFlow<Result<Unit>?> = _registerResult

    private var currentUserId: String? = null
    private var currentUsername: String? = null

    fun initialize(context: Context) {
        databaseProvider = DatabaseProvider.getInstance(context)
    }

    fun register(nombre: String, nombrePila: String, password: String) {
        viewModelScope.launch {
            try {
                // Verificar si el usuario ya existe
                val existingUser = databaseProvider?.usuarioDao?.getUsuarioByNombre(nombre)
                if (existingUser != null) {
                    _registerResult.value = Result.failure(RegistroError.UsuarioExistente)
                    return@launch
                }

                val userId = UUID.randomUUID().toString()
                val usuario = Usuario(
                    id_usr = userId,
                    nombre_usr = nombre,
                    nombre_pila = nombrePila,
                    contrasena_usr = password,
                    fecha_modificacion = System.currentTimeMillis(),
                    eliminado_estado = false,
                    sincronizado = false
                )

                // Guardar en la base de datos local
                databaseProvider?.usuarioDao?.insertUsuario(usuario)

                // Enviar al servidor
                val response = api.register(usuario)
                if (response.isSuccessful) {
                    currentUserId = userId
                    currentUsername = nombre
                    _registerResult.value = Result.success(Unit)
                } else {
                    _registerResult.value = Result.failure(RegistroError.ErrorGenerico("Error al registrar usuario"))
                }
            } catch (e: Exception) {
                _registerResult.value = Result.failure(RegistroError.ErrorGenerico(e.message ?: "Error al registrar usuario"))
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = api.login(LoginRequest(username, password))
                if (response.isSuccessful && response.body() != null) {
                    currentUserId = response.body()?.userId
                    currentUsername = username
                    _loginResult.value = Result.success(response.body()!!)
                } else {
                    _loginResult.value = Result.failure(Exception("Error al iniciar sesión"))
                }
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }

    fun getCurrentUserId(): String? = currentUserId

    fun getCurrentUsername(): String? = currentUsername

    fun logout() {
        currentUserId = null
        currentUsername = null
    }
} 