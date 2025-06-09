package com.example.tianguisuni.data.dao

import androidx.room.*
import com.example.tianguisuni.data.entities.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * DAO para la entidad Usuario que maneja las operaciones de base de datos.
 */
@Dao
interface UsuarioDao {
    /**
     * Inserta un nuevo usuario
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: Usuario)

    /**
     * Inserta una lista de usuarios
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuarios(usuarios: List<Usuario>)

    /**
     * Obtiene un usuario por su nombre de usuario
     */
    @Query("SELECT * FROM usuarios WHERE nombre_usr = :nombreUsr AND eliminado_estado = 0")
    suspend fun getUsuarioByNombre(nombreUsr: String): Usuario?

    /**
     * Obtiene un usuario por su ID
     */
    @Query("SELECT * FROM usuarios WHERE id_usr = :userId AND eliminado_estado = 0")
    suspend fun getUsuarioById(userId: String): Usuario?

    /**
     * Actualiza la información de un usuario
     */
    @Update
    suspend fun updateUsuario(usuario: Usuario)

    /**
     * Marca un usuario como eliminado
     */
    @Query("UPDATE usuarios SET eliminado_estado = 1, fecha_modificacion = :timestamp WHERE id_usr = :userId")
    suspend fun deleteUsuario(userId: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Obtiene usuarios que necesitan sincronización
     */
    @Query("SELECT * FROM usuarios WHERE sincronizado = 0")
    suspend fun getUnsyncedUsuarios(): List<Usuario>

    /**
     * Marca usuarios como sincronizados
     */
    @Query("UPDATE usuarios SET sincronizado = 1 WHERE id_usr IN (:userIds)")
    suspend fun markAsSynced(userIds: List<String>)

    /**
     * Verifica las credenciales de un usuario
     */
    @Query("SELECT * FROM usuarios WHERE nombre_usr = :nombreUsr AND contrasena_usr = :contrasena AND eliminado_estado = 0")
    suspend fun verificarCredenciales(nombreUsr: String, contrasena: String): Usuario?

    /**
     * Obtiene todos los usuarios activos
     */
    @Query("SELECT * FROM usuarios WHERE eliminado_estado = 0")
    fun getAllUsuarios(): Flow<List<Usuario>>
} 