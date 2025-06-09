package com.example.tianguisuni.data.network.models

import com.example.tianguisuni.data.entities.Publicacion
import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("nombre_usr") val nombreUsr: String,
    @SerializedName("contrasena_usr") val contrasenaUsr: String
)

data class LoginResponse(
    val success: Boolean,
    @SerializedName("user_id") val userId: String?
)

data class SyncResponse(
    @SerializedName("serverRecords") val serverRecords: List<Publicacion>,
    val updates: List<Publicacion>
) 