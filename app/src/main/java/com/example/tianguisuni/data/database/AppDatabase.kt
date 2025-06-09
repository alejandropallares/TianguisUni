package com.example.tianguisuni.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tianguisuni.data.dao.PublicacionDao
import com.example.tianguisuni.data.dao.UsuarioDao
import com.example.tianguisuni.data.entities.Publicacion
import com.example.tianguisuni.data.entities.Usuario

/**
 * Base de datos principal de la aplicación.
 * Utiliza el patrón Singleton para garantizar una única instancia.
 */
@Database(
    entities = [
        Usuario::class,
        Publicacion::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    
    // DAOs
    abstract fun usuarioDao(): UsuarioDao
    abstract fun publicacionDao(): PublicacionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "tianguis_db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration() // En desarrollo, en producción deberíamos manejar migraciones
                .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
} 