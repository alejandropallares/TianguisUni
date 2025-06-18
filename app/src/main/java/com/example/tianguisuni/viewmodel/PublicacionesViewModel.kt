package com.example.tianguisuni.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tianguisuni.data.database.DatabaseProvider
import com.example.tianguisuni.data.entities.Publicacion
import com.example.tianguisuni.data.entities.Usuario
import com.example.tianguisuni.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.coroutines.flow.collect

class PublicacionesViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitClient.apiService
    private val databaseProvider = DatabaseProvider.getInstance(application)
    private val publicacionDao = databaseProvider.publicacionDao
    private val usuarioDao = databaseProvider.usuarioDao

    private val _publicaciones = MutableStateFlow<List<PublicacionConUsuario>>(emptyList())
    val publicaciones: StateFlow<List<PublicacionConUsuario>> = _publicaciones.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    data class PublicacionConUsuario(
        val publicacion: Publicacion,
        val nombreUsuario: String
    )

    init {
        cargarPublicaciones()
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                _isRefreshing.value = true
                _error.value = null

                // Obtener publicaciones del servidor
                val publicacionesServidor = api.getPublicaciones()
                
                // Guardar publicaciones en la base de datos local
                publicacionDao.insertPublicaciones(publicacionesServidor)

                // Crear lista de PublicacionConUsuario usando el nombre_pila de la respuesta y ordenar por fecha
                val publicacionesConUsuario = publicacionesServidor
                    .sortedByDescending { it.fecha_modificacion }
                    .map { publicacion ->
                        PublicacionConUsuario(
                            publicacion = publicacion,
                            nombreUsuario = publicacion.nombre_pila ?: "Usuario desconocido"
                        )
                    }

                _publicaciones.value = publicacionesConUsuario
            } catch (e: Exception) {
                Log.e("PublicacionesViewModel", "Error al recargar publicaciones", e)
                _error.value = "Error al recargar las publicaciones"
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun cargarPublicaciones() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Obtener publicaciones del servidor
                val publicacionesServidor = api.getPublicaciones()
                
                // Guardar publicaciones en la base de datos local
                publicacionDao.insertPublicaciones(publicacionesServidor)

                // Crear lista de PublicacionConUsuario usando el nombre_pila de la respuesta y ordenar por fecha
                val publicacionesConUsuario = publicacionesServidor
                    .sortedByDescending { it.fecha_modificacion }
                    .map { publicacion ->
                        PublicacionConUsuario(
                            publicacion = publicacion,
                            nombreUsuario = publicacion.nombre_pila ?: "Usuario desconocido"
                        )
                    }

                _publicaciones.value = publicacionesConUsuario
            } catch (e: Exception) {
                Log.e("PublicacionesViewModel", "Error al cargar publicaciones", e)
                _error.value = "Error al cargar las publicaciones. Mostrando datos locales."
                
                // En caso de error, cargar datos locales
                publicacionDao.getAllPublicaciones().collect { publicacionesLocales ->
                    val publicacionesConUsuario = publicacionesLocales
                        .sortedByDescending { it.fecha_modificacion }
                        .map { publicacion ->
                            PublicacionConUsuario(
                                publicacion = publicacion,
                                nombreUsuario = publicacion.nombre_pila ?: "Usuario desconocido"
                            )
                        }
                    _publicaciones.value = publicacionesConUsuario
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filtrarPorCategoria(categoria: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                if (categoria == "Todo") {
                    publicacionDao.getAllPublicaciones().collect { publicaciones ->
                        val publicacionesConUsuario = publicaciones
                            .sortedByDescending { it.fecha_modificacion }
                            .map { publicacion ->
                                PublicacionConUsuario(
                                    publicacion = publicacion,
                                    nombreUsuario = publicacion.nombre_pila ?: "Usuario desconocido"
                                )
                            }
                        _publicaciones.value = publicacionesConUsuario
                    }
                } else {
                    publicacionDao.getPublicacionesByCategoria(categoria).collect { publicaciones ->
                        val publicacionesConUsuario = publicaciones
                            .sortedByDescending { it.fecha_modificacion }
                            .map { publicacion ->
                                PublicacionConUsuario(
                                    publicacion = publicacion,
                                    nombreUsuario = publicacion.nombre_pila ?: "Usuario desconocido"
                                )
                            }
                        _publicaciones.value = publicacionesConUsuario
                    }
                }
            } catch (e: Exception) {
                Log.e("PublicacionesViewModel", "Error al filtrar publicaciones", e)
                _error.value = "Error al filtrar las publicaciones"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 