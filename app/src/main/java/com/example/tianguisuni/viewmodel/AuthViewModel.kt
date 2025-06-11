package com.example.tianguisuni.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tianguisuni.data.database.DatabaseProvider
import com.example.tianguisuni.data.entities.Usuario
import com.example.tianguisuni.data.network.ApiService
import com.example.tianguisuni.data.network.RetrofitClient
import com.example.tianguisuni.data.network.models.LoginRequest
import com.example.tianguisuni.data.network.models.LoginResponse
import com.example.tianguisuni.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel(
    databaseProvider: DatabaseProvider,
    private val usuarioRepository: UsuarioRepository = UsuarioRepository(databaseProvider),
    private val apiService: ApiService = RetrofitClient.apiService
) : ViewModel() {

    private val _registerResult = MutableSharedFlow<Result<Unit>>()
    val registerResult: SharedFlow<Result<Unit>> = _registerResult

    private val _loginResult = MutableSharedFlow<Result<LoginResponse>>()
    val loginResult: SharedFlow<Result<LoginResponse>> = _loginResult

    fun register(usuario: Usuario) {
        viewModelScope.launch {
            try {
                // Primero intentamos registrar en el servidor
                val response = apiService.register(usuario)
                if (response.isSuccessful) {
                    // Si el registro en el servidor es exitoso, guardamos localmente
                    usuarioRepository.insertUsuario(usuario)
                    _registerResult.emit(Result.success(Unit))
                } else {
                    _registerResult.emit(Result.failure(Exception("Error al registrar: ${response.message()}")))
                }
            } catch (e: Exception) {
                _registerResult.emit(Result.failure(e))
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val loginRequest = LoginRequest(username, password)
                val response = apiService.login(loginRequest)
                
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    if (loginResponse.success && loginResponse.userId != null) {
                        // Obtenemos el usuario de la base de datos local o creamos uno nuevo
                        val usuario = Usuario(
                            id_usr = loginResponse.userId,
                            nombre_usr = username,
                            nombre_pila = "", // Se actualizará después con los datos completos
                            contrasena_usr = password
                        )
                        usuarioRepository.insertUsuario(usuario)
                        _loginResult.emit(Result.success(loginResponse))
                    } else {
                        _loginResult.emit(Result.failure(Exception(loginResponse.error ?: "Error desconocido al iniciar sesión")))
                    }
                } else {
                    _loginResult.emit(Result.failure(Exception("Error al iniciar sesión: ${response.message()}")))
                }
            } catch (e: Exception) {
                _loginResult.emit(Result.failure(e))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                usuarioRepository.deleteAllUsuarios()
            } catch (e: Exception) {
                // Manejar error de logout si es necesario
            }
        }
    }
} 