package com.example.tianguisuni.data.dao

import androidx.room.*
import com.example.tianguisuni.data.entities.Publicacion
import kotlinx.coroutines.flow.Flow

/**
 * DAO para la entidad Publicacion que maneja las operaciones de base de datos.
 */
@Dao
interface PublicacionDao {
    /**
     * Obtiene todas las publicaciones activas (no eliminadas)
     * Usando Flow para observar cambios en tiempo real
     */
    @Query("SELECT * FROM publicaciones WHERE eliminado_estado = 0 ORDER BY fecha_modificacion DESC")
    fun getAllPublicaciones(): Flow<List<Publicacion>>

    /**
     * Obtiene todas las publicaciones de un usuario específico
     */
    @Query("SELECT * FROM publicaciones WHERE user_id = :userId AND eliminado_estado = 0 ORDER BY fecha_modificacion DESC")
    fun getUserPublicaciones(userId: String): Flow<List<Publicacion>>

    /**
     * Obtiene una publicación específica por su UUID
     */
    @Query("SELECT * FROM publicaciones WHERE uuid = :uuid AND eliminado_estado = 0")
    suspend fun getPublicacionById(uuid: String): Publicacion?

    /**
     * Inserta una lista de publicaciones
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPublicaciones(publicaciones: List<Publicacion>)

    /**
     * Inserta una única publicación
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPublicacion(publicacion: Publicacion)

    /**
     * Actualiza una publicación existente
     */
    @Update
    suspend fun updatePublicacion(publicacion: Publicacion)

    /**
     * Marca una publicación como eliminada
     */
    @Query("UPDATE publicaciones SET eliminado_estado = 1, fecha_modificacion = :timestamp WHERE uuid = :uuid")
    suspend fun deletePublicacion(uuid: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Obtiene publicaciones que necesitan sincronización
     */
    @Query("SELECT * FROM publicaciones WHERE sincronizado = 0")
    suspend fun getUnsyncedPublicaciones(): List<Publicacion>

    /**
     * Marca publicaciones como sincronizadas
     */
    @Query("UPDATE publicaciones SET sincronizado = 1 WHERE uuid IN (:uuids)")
    suspend fun markAsSynced(uuids: List<String>)

    /**
     * Busca publicaciones por categoría
     */
    @Query("SELECT * FROM publicaciones WHERE categoria_producto = :categoria AND eliminado_estado = 0 ORDER BY fecha_modificacion DESC")
    fun getPublicacionesByCategoria(categoria: String): Flow<List<Publicacion>>

    /**
     * Busca publicaciones por término de búsqueda en nombre o descripción
     */
    @Query("SELECT * FROM publicaciones WHERE (nombre_producto LIKE '%' || :searchTerm || '%' OR descripcion_producto LIKE '%' || :searchTerm || '%') AND eliminado_estado = 0 ORDER BY fecha_modificacion DESC")
    fun searchPublicaciones(searchTerm: String): Flow<List<Publicacion>>
} 