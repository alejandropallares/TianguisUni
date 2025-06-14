package com.example.tianguisuni.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.AndroidViewModel
import com.example.tianguisuni.data.database.DatabaseProvider
import com.example.tianguisuni.data.entities.Publicacion
import com.example.tianguisuni.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class PublicacionViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitClient.apiService
    private val databaseProvider = DatabaseProvider.getInstance(application)
    private val publicacionDao = databaseProvider.publicacionDao

    private val _publicaciones = MutableStateFlow<List<Publicacion>>(emptyList())
    val publicaciones: StateFlow<List<Publicacion>> = _publicaciones.asStateFlow()

    private val _operationResult = MutableLiveData<Result<Unit>>()
    val operationResult: LiveData<Result<Unit>> = _operationResult

    private val _syncResult = MutableLiveData<Result<Unit>>()
    val syncResult: LiveData<Result<Unit>> = _syncResult

    private var observerJob: Job? = null

    fun startObservingPublicaciones(userId: String) {
        Log.d("PublicacionViewModel", "Iniciando observación para usuario: $userId")
        // Cancelar el job anterior si existe
        observerJob?.cancel()
        
        // Iniciar nueva observación
        observerJob = viewModelScope.launch {
            publicacionDao.getUserPublicaciones(userId).collectLatest { lista ->
                Log.d("PublicacionViewModel", "Recibidas ${lista.size} publicaciones de Room")
                lista.forEach { pub ->
                    Log.d("PublicacionViewModel", "Publicación: ${pub.nombre_producto}, ID: ${pub.uuid}, UserID: ${pub.user_id}")
                }
                _publicaciones.value = lista.filter { !it.eliminado_estado }
                Log.d("PublicacionViewModel", "Estado actualizado con ${_publicaciones.value.size} publicaciones")
            }
        }
        
        // Cargar datos del servidor
        cargarPublicaciones(userId)
    }

    private fun cargarPublicaciones(userId: String) {
        Log.d("PublicacionViewModel", "Cargando publicaciones del servidor")
        viewModelScope.launch {
            try {
                // Obtener publicaciones del servidor
                val response = api.getPublicaciones()
                Log.d("PublicacionViewModel", "Recibidas ${response.size} publicaciones del servidor")
                // Filtrar solo las publicaciones del usuario actual
                val publicacionesUsuario = response.filter { it.user_id == userId }
                Log.d("PublicacionViewModel", "Filtradas ${publicacionesUsuario.size} publicaciones del usuario")
                // Guardar en la base de datos local
                publicacionDao.insertPublicaciones(publicacionesUsuario)
                Log.d("PublicacionViewModel", "Publicaciones guardadas en Room")
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al cargar publicaciones", e)
                // Si hay error, seguimos mostrando los datos locales
                e.printStackTrace()
            }
        }
    }

    fun crearPublicacion(publicacion: Publicacion) {
        viewModelScope.launch {
            try {
                val response = api.createPublicacion(publicacion)
                if (response.isSuccessful) {
                    _operationResult.value = Result.success(Unit)
                    startObservingPublicaciones(publicacion.user_id) // Recargar la lista
                } else {
                    _operationResult.value = Result.failure(Exception("Error al crear la publicación"))
                }
            } catch (e: Exception) {
                _operationResult.value = Result.failure(e)
            }
        }
    }

    fun actualizarPublicacion(publicacion: Publicacion) {
        viewModelScope.launch {
            try {
                val response = api.updatePublicacion(publicacion.uuid, publicacion)
                if (response.isSuccessful) {
                    _operationResult.value = Result.success(Unit)
                    startObservingPublicaciones(publicacion.user_id) // Recargar la lista
                } else {
                    _operationResult.value = Result.failure(Exception("Error al actualizar la publicación"))
                }
            } catch (e: Exception) {
                _operationResult.value = Result.failure(e)
            }
        }
    }

    fun eliminarPublicacion(uuid: String, userId: String) {
        viewModelScope.launch {
            try {
                Log.d("PublicacionViewModel", "Iniciando eliminación lógica de publicación: $uuid")
                
                // Obtener la publicación actual de Room
                val publicacionActual = publicacionDao.getPublicacionById(uuid)
                if (publicacionActual != null) {
                    // Crear copia con eliminado_estado = true
                    val publicacionEliminada = publicacionActual.copy(
                        eliminado_estado = true,
                        fecha_modificacion = System.currentTimeMillis()
                    )

                    // Actualizar en el servidor
                    val response = api.updatePublicacion(uuid, publicacionEliminada)
                    if (response.isSuccessful) {
                        // Actualizar en Room
                        publicacionDao.insertPublicacion(publicacionEliminada)
                        Log.d("PublicacionViewModel", "Publicación marcada como eliminada exitosamente")
                        _operationResult.value = Result.success(Unit)
                        startObservingPublicaciones(userId) // Recargar la lista
                    } else {
                        Log.e("PublicacionViewModel", "Error al marcar como eliminada en el servidor")
                        _operationResult.value = Result.failure(Exception("Error al eliminar la publicación"))
                    }
                } else {
                    Log.e("PublicacionViewModel", "No se encontró la publicación en Room")
                    _operationResult.value = Result.failure(Exception("Publicación no encontrada"))
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al eliminar publicación", e)
                _operationResult.value = Result.failure(e)
            }
        }
    }

    fun sincronizarPublicaciones(publicacionesLocales: List<Publicacion>) {
        viewModelScope.launch {
            try {
                val response = api.syncPublicaciones(publicacionesLocales)
                // Aquí podrías actualizar la base de datos local con response.serverRecords y response.updates
                _syncResult.value = Result.success(Unit)
            } catch (e: Exception) {
                _syncResult.value = Result.failure(e)
            }
        }
    }
} 