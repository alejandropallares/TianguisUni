package com.example.tianguisuni.data.database

import android.content.Context
import com.example.tianguisuni.data.dao.PublicacionDao
import com.example.tianguisuni.data.dao.UsuarioDao

/**
 * Proveedor de acceso a la base de datos y sus DAOs.
 * Esta clase facilita el acceso a los DAOs y la gestión de la base de datos.
 */
class DatabaseProvider private constructor(context: Context) {
    
    private val database = AppDatabase.getInstance(context)
    
    // DAOs
    val usuarioDao: UsuarioDao = database.usuarioDao()
    val publicacionDao: PublicacionDao = database.publicacionDao()

    companion object {
        @Volatile
        private var instance: DatabaseProvider? = null

        fun getInstance(context: Context): DatabaseProvider {
            return instance ?: synchronized(this) {
                instance ?: DatabaseProvider(context).also { instance = it }
            }
        }
    }
} 