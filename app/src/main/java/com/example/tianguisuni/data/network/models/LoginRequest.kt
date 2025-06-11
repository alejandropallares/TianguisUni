package com.example.tianguisuni.data.network.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo para la solicitud de inicio de sesión.
 * Los nombres de los campos coinciden con los esperados por el backend PHP.
 */
data class LoginRequest(
    @SerializedName("nombre_usr")
    val username: String,
    
    @SerializedName("contrasena_usr")
    val password: String
) 