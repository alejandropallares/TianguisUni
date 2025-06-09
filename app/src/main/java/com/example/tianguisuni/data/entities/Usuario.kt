package com.example.tianguisuni.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad que representa la tabla de usuarios en la base de datos local.
 *
 * @property id_usr Identificador único del usuario
 * @property nombre_usr Nombre de usuario para inicio de sesión
 * @property nombre_pila Nombre real del usuario
 * @property contrasena_usr Contraseña del usuario (se recomienda almacenar hasheada)
 * @property fecha_modificacion Timestamp de la última modificación del registro
 * @property eliminado_estado Indica si el registro está marcado como eliminado
 * @property sincronizado Indica si el registro está sincronizado con el servidor
 */
@Entity(
    tableName = "usuarios",
    indices = [
        Index(value = ["nombre_usr"], unique = true)
    ]
)
data class Usuario(
    @PrimaryKey 
    val id_usr: String,
    
    val nombre_usr: String,
    val nombre_pila: String,
    val contrasena_usr: String,
    
    // Campos para sincronización
    val fecha_modificacion: Long = System.currentTimeMillis(),
    val eliminado_estado: Boolean = false,
    val sincronizado: Boolean = false
) 