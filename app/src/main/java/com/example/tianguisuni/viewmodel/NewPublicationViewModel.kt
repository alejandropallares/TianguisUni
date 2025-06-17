package com.example.tianguisuni.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
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
    private var appContext: Context? = null
    
    private val _state = MutableStateFlow<NewPublicationState>(NewPublicationState.Initial)
    val state: StateFlow<NewPublicationState> = _state.asStateFlow()

    private val _formState = MutableStateFlow(NewPublicationFormState())
    val formState: StateFlow<NewPublicationFormState> = _formState.asStateFlow()

    fun initialize(context: Context) {
        databaseProvider = DatabaseProvider.getInstance(context)
        appContext = context.applicationContext
    }

    fun updateName(name: String) {
        val capitalizedName = if (name.isNotEmpty()) name.replaceFirstChar { it.uppercase() } else name
        if (capitalizedName.length <= 30) {
            _formState.value = _formState.value.copy(
                name = capitalizedName,
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
        val capitalizedDescription = if (description.isNotEmpty()) description.replaceFirstChar { it.uppercase() } else description
        if (capitalizedDescription.length <= 200) {
            _formState.value = _formState.value.copy(
                description = capitalizedDescription,
                descriptionError = null
            )
        } else {
            _formState.value = _formState.value.copy(
                descriptionError = "Máximo 200 caracteres"
            )
        }
    }

    fun updateLocation(location: String) {
        val capitalizedLocation = if (location.isNotEmpty()) location.replaceFirstChar { it.uppercase() } else location
        if (capitalizedLocation.length <= 30) {
            _formState.value = _formState.value.copy(
                location = capitalizedLocation,
                locationError = null
            )
        } else {
            _formState.value = _formState.value.copy(
                locationError = "Máximo 30 caracteres"
            )
        }
    }

    fun updatePrice(price: String) {
        if (price.isEmpty() || price.matches(Regex("^\\d*\\.?\\d*$"))) {
            _formState.value = _formState.value.copy(
                price = price,
                priceError = null
            )
        }
    }

    fun updateImage(uri: Uri?) {
        viewModelScope.launch {
            if (uri != null) {
                try {
                    // Verificar el tamaño antes de actualizar el estado
                    val inputStream = appContext?.contentResolver?.openInputStream(uri)
                    val byteArray = inputStream?.readBytes()
                    
                    if (byteArray != null && byteArray.size > 20 * 1024) {
                        _formState.value = _formState.value.copy(
                            imageUri = null,
                            imageError = "La imagen excede el límite de 20KB. Por favor, elige una imagen más pequeña."
                        )
                    } else {
                        _formState.value = _formState.value.copy(
                            imageUri = uri.toString(),
                            imageError = null
                        )
                    }
                    inputStream?.close()
                } catch (e: Exception) {
                    _formState.value = _formState.value.copy(
                        imageUri = null,
                        imageError = "Error al procesar la imagen"
                    )
                }
            } else {
                _formState.value = _formState.value.copy(
                    imageUri = null,
                    imageError = null
                )
            }
        }
    }

    fun resetForm() {
        _formState.value = NewPublicationFormState()
        _state.value = NewPublicationState.Initial
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
        val byteArray = inputStream?.readBytes() ?: throw IllegalStateException("No se pudo leer la imagen")
        
        // Verificar el tamaño de la imagen (20KB = 20 * 1024 bytes)
        if (byteArray.size > 20 * 1024) {
            throw ImageTooLargeException("La imagen excede el límite de 20KB. Por favor, elige una imagen más pequeña.")
        }

        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    class ImageTooLargeException(message: String) : Exception(message)

    fun createPublication(context: Context, userId: String) {
        if (!validateForm()) return

        viewModelScope.launch {
            try {
                _state.value = NewPublicationState.Loading
                Log.d("NewPublicationViewModel", "Creando nueva publicación para usuario: $userId")
                
                val formState = _formState.value
                val imageBase64 = formState.imageUri?.let { 
                    convertImageToBase64(context, Uri.parse(it))
                } ?: throw IllegalStateException("Image URI is null")

                // Obtener el usuario actual para el nombre_pila
                val usuario = databaseProvider?.usuarioDao?.getUsuarioById(userId)

                val publicacion = Publicacion(
                    uuid = UUID.randomUUID().toString(),
                    nombre_producto = formState.name,
                    categoria_producto = formState.category,
                    descripcion_producto = formState.description,
                    ubicacion_producto = formState.location,
                    precio_producto = formState.price.toDoubleOrNull() ?: 0.0,
                    imagen_producto = imageBase64,
                    user_id = userId,
                    nombre_pila = usuario?.nombre_pila ?: "Usuario desconocido",
                    fecha_modificacion = System.currentTimeMillis(),
                    eliminado_estado = false,
                    sincronizado = false
                )

                Log.d("NewPublicationViewModel", "Publicación creada: ${publicacion.uuid}, Nombre: ${publicacion.nombre_producto}")

                // Primero intentamos enviar al servidor
                val response = api.createPublicacion(publicacion)
                if (response.isSuccessful) {
                    Log.d("NewPublicationViewModel", "Publicación guardada en servidor exitosamente")
                    // Si el servidor acepta la publicación, la guardamos localmente
                    databaseProvider?.publicacionDao?.insertPublicacion(publicacion.copy(sincronizado = true))
                    Log.d("NewPublicationViewModel", "Publicación guardada en Room exitosamente")
                    _state.value = NewPublicationState.Success
                } else {
                    Log.e("NewPublicationViewModel", "Error al guardar en servidor, guardando localmente")
                    // Si hay error en el servidor, guardamos localmente pero marcada como no sincronizada
                    databaseProvider?.publicacionDao?.insertPublicacion(publicacion)
                    _state.value = NewPublicationState.Error("Error al crear la publicación en el servidor. Se guardó localmente y se sincronizará más tarde.")
                }
            } catch (e: Exception) {
                Log.e("NewPublicationViewModel", "Error al crear publicación", e)
                // En caso de error de red, guardamos localmente
                try {
                    val formState = _formState.value
                    val imageBase64 = formState.imageUri?.let { 
                        convertImageToBase64(context, Uri.parse(it))
                    } ?: throw IllegalStateException("Image URI is null")

                    // Obtener el usuario actual para el nombre_pila
                    val usuario = databaseProvider?.usuarioDao?.getUsuarioById(userId)

                    val publicacion = Publicacion(
                        uuid = UUID.randomUUID().toString(),
                        nombre_producto = formState.name,
                        categoria_producto = formState.category,
                        descripcion_producto = formState.description,
                        ubicacion_producto = formState.location,
                        precio_producto = formState.price.toDoubleOrNull() ?: 0.0,
                        imagen_producto = imageBase64,
                        user_id = userId,
                        nombre_pila = usuario?.nombre_pila ?: "Usuario desconocido",
                        fecha_modificacion = System.currentTimeMillis(),
                        eliminado_estado = false,
                        sincronizado = false
                    )
                    
                    Log.d("NewPublicationViewModel", "Guardando publicación localmente después de error")
                    databaseProvider?.publicacionDao?.insertPublicacion(publicacion)
                    _state.value = NewPublicationState.Error("Error de conexión. La publicación se guardó localmente y se sincronizará cuando haya conexión.")
                } catch (e: Exception) {
                    Log.e("NewPublicationViewModel", "Error al guardar localmente", e)
                    _state.value = NewPublicationState.Error("Error al crear la publicación: ${e.message}")
                }
            }
        }
    }
} 