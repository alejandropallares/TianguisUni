package com.example.tianguisuni.data.network.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo para la respuesta del inicio de sesión.
 * 
 * @property success Indica si el inicio de sesión fue exitoso
 * @property userId ID del usuario si el inicio de sesión fue exitoso
 * @property error Mensaje de error si el inicio de sesión falló
 */
data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("user_id")
    val userId: String?,
    
    @SerializedName("error")
    val error: String? = null
) 