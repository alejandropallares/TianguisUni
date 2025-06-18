package com.example.tianguisuni.viewmodel

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.createSavedStateHandle
import com.example.tianguisuni.model.PublicacionDetalle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetallesPublicacionViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val publicacionDetalle: PublicacionDetalle = checkNotNull(savedStateHandle["publicacion"])
    
    private val _imageBitmap = MutableStateFlow<ImageBitmap?>(null)
    val imageBitmap: StateFlow<ImageBitmap?> = _imageBitmap

    init {
        loadImage()
    }

    private fun loadImage() {
        viewModelScope.launch {
            try {
                publicacionDetalle.imagenProductoBase64?.let { base64String ->
                    val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                    _imageBitmap.value = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)?.asImageBitmap()
                }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    companion object {
        fun provideFactory(publicacionDetalle: PublicacionDetalle): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val savedStateHandle = extras.createSavedStateHandle()
                savedStateHandle["publicacion"] = publicacionDetalle
                return DetallesPublicacionViewModel(savedStateHandle) as T
            }
        }
    }
} 