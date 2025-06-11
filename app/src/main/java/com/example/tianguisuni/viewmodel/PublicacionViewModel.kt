package com.example.tianguisuni.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tianguisuni.data.entities.Publicacion
import com.example.tianguisuni.data.network.RetrofitClient
import kotlinx.coroutines.launch

class PublicacionViewModel : ViewModel() {
    private val api = RetrofitClient.apiService

    private val _publicaciones = MutableLiveData<List<Publicacion>>()
    val publicaciones: LiveData<List<Publicacion>> = _publicaciones

    private val _operationResult = MutableLiveData<Result<Unit>>()
    val operationResult: LiveData<Result<Unit>> = _operationResult

    private val _syncResult = MutableLiveData<Result<Unit>>()
    val syncResult: LiveData<Result<Unit>> = _syncResult

    fun cargarPublicaciones() {
        viewModelScope.launch {
            try {
                val response = api.getPublicaciones()
                _publicaciones.value = response
            } catch (e: Exception) {
                _operationResult.value = Result.failure(e)
            }
        }
    }

    fun crearPublicacion(publicacion: Publicacion) {
        viewModelScope.launch {
            try {
                val response = api.createPublicacion(publicacion)
                if (response.isSuccessful) {
                    _operationResult.value = Result.success(Unit)
                    cargarPublicaciones() // Recargar la lista
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
                    cargarPublicaciones() // Recargar la lista
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
                val response = api.deletePublicacion(uuid, userId)
                if (response.isSuccessful) {
                    _operationResult.value = Result.success(Unit)
                    cargarPublicaciones() // Recargar la lista
                } else {
                    _operationResult.value = Result.failure(Exception("Error al eliminar la publicación"))
                }
            } catch (e: Exception) {
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