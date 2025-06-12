package com.example.tianguisuni.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tianguisuni.data.database.DatabaseProvider
import com.example.tianguisuni.data.entities.Publicacion
import com.example.tianguisuni.data.entities.Usuario
import com.example.tianguisuni.data.network.RetrofitClient
import com.example.tianguisuni.ui.state.NewPublicationFormState
import com.example.tianguisuni.ui.state.NewPublicationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID

class NewPublicationViewModel : ViewModel() {
    private val api = RetrofitClient.apiService
    private var databaseProvider: DatabaseProvider? = null
    
    private val _state = MutableStateFlow<NewPublicationState>(NewPublicationState.Initial)
    val state: StateFlow<NewPublicationState> = _state.asStateFlow()

    private val _formState = MutableStateFlow(NewPublicationFormState())
    val formState: StateFlow<NewPublicationFormState> = _formState.asStateFlow()

    fun initialize(context: Context) {
        databaseProvider = DatabaseProvider.getInstance(context)
    }

    fun updateName(name: String) {
        if (name.length <= 30) {
            _formState.value = _formState.value.copy(
                name = name,
                nameError = null
            )
        } else {
            _formState.value = _formState.value.copy(
                nameError = "Máximo 30 caracteres"
            )
        }
    }

    fun updateCategory(category: String) {
        _formState.value = _formState.value.copy(
            category = category,
            categoryError = null
        )
    }

    fun updateDescription(description: String) {
        if (description.length <= 200) {
            _formState.value = _formState.value.copy(
                description = description,
                descriptionError = null
            )
        } else {
            _formState.value = _formState.value.copy(
                descriptionError = "Máximo 200 caracteres"
            )
        }
    }

    fun updateLocation(location: String) {
        if (location.length <= 30) {
            _formState.value = _formState.value.copy(
                location = location,
                locationError = null
            )
        } else {
            _formState.value = _formState.value.copy(
                locationError = "Máximo 30 caracteres"
            )
        }
    }

    fun updatePrice(price: String) {
        val regex = Regex("^\\d*\\.?\\d{0,2}$")
        if (price.isEmpty() || regex.matches(price)) {
            _formState.value = _formState.value.copy(
                price = price,
                priceError = null
            )
        } else {
            _formState.value = _formState.value.copy(
                priceError = "Formato inválido. Ejemplo: 99.99"
            )
        }
    }

    fun updateImage(uri: Uri?) {
        _formState.value = _formState.value.copy(
            imageUri = uri?.toString(),
            imageError = null
        )
    }

    private fun validateForm(): Boolean {
        var isValid = true
        val currentState = _formState.value

        if (currentState.name.isBlank()) {
            _formState.value = currentState.copy(nameError = "Campo requerido")
            isValid = false
        }

        if (currentState.category.isBlank()) {
            _formState.value = currentState.copy(categoryError = "Selecciona una categoría")
            isValid = false
        }

        if (currentState.description.isBlank()) {
            _formState.value = currentState.copy(descriptionError = "Campo requerido")
            isValid = false
        }

        if (currentState.location.isBlank()) {
            _formState.value = currentState.copy(locationError = "Campo requerido")
            isValid = false
        }

        if (currentState.price.isBlank()) {
            _formState.value = currentState.copy(priceError = "Campo requerido")
            isValid = false
        }

        if (currentState.imageUri == null) {
            _formState.value = currentState.copy(imageError = "Imagen requerida")
            isValid = false
        }

        return isValid
    }

    private suspend fun convertImageToBase64(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun createPublication(context: Context, userId: String) {
        if (!validateForm()) return

        viewModelScope.launch {
            try {
                _state.value = NewPublicationState.Loading
                
                val formState = _formState.value
                val imageBase64 = formState.imageUri?.let { 
                    convertImageToBase64(context, Uri.parse(it))
                } ?: throw IllegalStateException("Image URI is null")

                val publicacion = Publicacion(
                    uuid = UUID.randomUUID().toString(),
                    nombre_producto = formState.name,
                    categoria_producto = formState.category,
                    descripcion_producto = formState.description,
                    ubicacion_producto = formState.location,
                    precio_producto = formState.price.toDoubleOrNull() ?: 0.0,
                    imagen_producto = imageBase64,
                    user_id = userId,
                    fecha_modificacion = System.currentTimeMillis(),
                    eliminado_estado = false,
                    sincronizado = false
                )

                // Primero intentamos enviar al servidor
                val response = api.createPublicacion(publicacion)
                if (response.isSuccessful) {
                    // Si el servidor acepta la publicación, la guardamos localmente
                    databaseProvider?.publicacionDao?.insertPublicacion(publicacion.copy(sincronizado = true))
                    _state.value = NewPublicationState.Success
                } else {
                    // Si hay error en el servidor, guardamos localmente pero marcada como no sincronizada
                    databaseProvider?.publicacionDao?.insertPublicacion(publicacion)
                    _state.value = NewPublicationState.Error("Error al crear la publicación en el servidor. Se guardó localmente y se sincronizará más tarde.")
                }
            } catch (e: Exception) {
                // En caso de error de red, guardamos localmente
                try {
                    val formState = _formState.value
                    val imageBase64 = formState.imageUri?.let { 
                        convertImageToBase64(context, Uri.parse(it))
                    } ?: throw IllegalStateException("Image URI is null")

                    val publicacion = Publicacion(
                        uuid = UUID.randomUUID().toString(),
                        nombre_producto = formState.name,
                        categoria_producto = formState.category,
                        descripcion_producto = formState.description,
                        ubicacion_producto = formState.location,
                        precio_producto = formState.price.toDoubleOrNull() ?: 0.0,
                        imagen_producto = imageBase64,
                        user_id = userId,
                        fecha_modificacion = System.currentTimeMillis(),
                        eliminado_estado = false,
                        sincronizado = false
                    )
                    
                    databaseProvider?.publicacionDao?.insertPublicacion(publicacion)
                    _state.value = NewPublicationState.Error("Error de conexión. La publicación se guardó localmente y se sincronizará cuando haya conexión.")
                } catch (e: Exception) {
                    _state.value = NewPublicationState.Error("Error al crear la publicación: ${e.message}")
                }
            }
        }
    }

    fun resetState() {
        _state.value = NewPublicationState.Initial
        _formState.value = NewPublicationFormState()
    }
} 