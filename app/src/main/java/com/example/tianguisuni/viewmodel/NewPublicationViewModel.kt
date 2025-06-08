package com.example.tianguisuni.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tianguisuni.model.Publication
import com.example.tianguisuni.ui.state.NewPublicationFormState
import com.example.tianguisuni.ui.state.NewPublicationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class NewPublicationViewModel : ViewModel() {
    private val _state = MutableStateFlow<NewPublicationState>(NewPublicationState.Initial)
    val state: StateFlow<NewPublicationState> = _state.asStateFlow()

    private val _formState = MutableStateFlow(NewPublicationFormState())
    val formState: StateFlow<NewPublicationFormState> = _formState.asStateFlow()

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

    fun validateForm(): Boolean {
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

    fun createPublication() {
        if (!validateForm()) return

        viewModelScope.launch {
            try {
                _state.value = NewPublicationState.Loading
                
                val formState = _formState.value
                val publication = Publication(
                    name = formState.name,
                    category = formState.category,
                    description = formState.description,
                    location = formState.location,
                    price = formState.price.toDoubleOrNull() ?: 0.0,
                    imageUri = formState.imageUri?.let { Uri.parse(it) },
                    userId = "TODO" // TODO: Obtener el ID del usuario actual
                )

                // TODO: Guardar la publicación en la base de datos
                
                _state.value = NewPublicationState.Success
            } catch (e: Exception) {
                _state.value = NewPublicationState.Error(e.message ?: "Error al crear la publicación")
            }
        }
    }

    fun resetState() {
        _state.value = NewPublicationState.Initial
        _formState.value = NewPublicationFormState()
    }
} 