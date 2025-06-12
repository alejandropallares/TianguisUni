package com.example.tianguisuni.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad que representa la tabla de publicaciones en la base de datos local.
 *
 * @property uuid Identificador único de la publicación
 * @property nombre_producto Nombre del producto
 * @property categoria_producto Categoría del producto
 * @property descripcion_producto Descripción detallada del producto
 * @property ubicacion_producto Ubicación donde se encuentra el producto
 * @property precio_producto Precio del producto
 * @property imagen_producto Imagen del producto en formato Base64
 * @property user_id ID del usuario que creó la publicación
 * @property fecha_modificacion Timestamp de la última modificación
 * @property eliminado_estado Indica si la publicación está marcada como eliminada
 * @property sincronizado Indica si el registro está sincronizado con el servidor
 */
@Entity(
    tableName = "publicaciones",
    indices = [
        Index(value = ["categoria_producto"]),
        Index(value = ["fecha_modificacion"])
    ]
)
data class Publicacion(
    @PrimaryKey 
    val uuid: String,
    
    val nombre_producto: String,
    val categoria_producto: String,
    val descripcion_producto: String,
    val ubicacion_producto: String,
    val precio_producto: Double,
    val imagen_producto: String, // Base64
    
    val user_id: String,
    
    // Campos para sincronización
    val fecha_modificacion: Long = System.currentTimeMillis(),
    val eliminado_estado: Boolean = false,
    val sincronizado: Boolean = false
) 