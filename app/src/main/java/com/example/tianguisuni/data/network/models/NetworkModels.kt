package com.example.tianguisuni.data.network.models

import com.example.tianguisuni.data.entities.Publicacion
import com.google.gson.annotations.SerializedName
 
data class SyncResponse(
    @SerializedName("serverRecords") val serverRecords: List<Publicacion>,
    val updates: List<Publicacion>
) 