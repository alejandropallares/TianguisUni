package com.example.tianguisuni.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tianguisuni.data.database.DatabaseProvider
import com.example.tianguisuni.data.entities.Usuario
import com.example.tianguisuni.data.network.RetrofitClient
import com.example.tianguisuni.data.network.models.LoginRequest
import com.example.tianguisuni.data.network.models.LoginResponse
import kotlinx.coroutines.launch
import java.util.UUID

class AuthViewModel : ViewModel() {
    private val api = RetrofitClient.apiService
    private var databaseProvider: DatabaseProvider? = null
    
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _registerResult = MutableLiveData<Result<Unit>>()
    val registerResult: LiveData<Result<Unit>> = _registerResult

    private var currentUserId: String? = null

    fun initialize(context: Context) {
        databaseProvider = DatabaseProvider.getInstance(context)
    }

    fun register(nombre: String, nombrePila: String, password: String) {
        viewModelScope.launch {
            try {
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
                    _registerResult.value = Result.success(Unit)
                } else {
                    _registerResult.value = Result.failure(Exception("Error al registrar usuario"))
                }
            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = api.login(LoginRequest(username, password))
                if (response.isSuccessful && response.body() != null) {
                    currentUserId = response.body()?.userId
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

    fun logout() {
        currentUserId = null
    }
} 