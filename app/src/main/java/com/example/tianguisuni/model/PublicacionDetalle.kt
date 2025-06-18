package com.example.tianguisuni.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PublicacionDetalle(
    val id: Int,
    val nombreProducto: String,
    val nombrePila: String?,
    val categoriaProducto: String,
    val descripcionProducto: String,
    val precioProducto: Double,
    val ubicacionProducto: String,
    val imagenProductoBase64: String?
) : Parcelable 