package com.example.tianguisuni.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 3,
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

        // Migración de la versión 1 a la 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Recrear la tabla de publicaciones sin la foreign key
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS publicaciones_new (
                        uuid TEXT NOT NULL PRIMARY KEY,
                        nombre_producto TEXT NOT NULL,
                        categoria_producto TEXT NOT NULL,
                        descripcion_producto TEXT NOT NULL,
                        ubicacion_producto TEXT NOT NULL,
                        precio_producto REAL NOT NULL,
                        imagen_producto TEXT NOT NULL,
                        user_id TEXT NOT NULL,
                        fecha_modificacion INTEGER NOT NULL,
                        eliminado_estado INTEGER NOT NULL,
                        sincronizado INTEGER NOT NULL
                    )
                """)

                // Copiar los datos de la tabla antigua a la nueva
                db.execSQL("""
                    INSERT INTO publicaciones_new 
                    SELECT * FROM publicaciones
                """)

                // Eliminar la tabla antigua
                db.execSQL("DROP TABLE publicaciones")

                // Renombrar la nueva tabla
                db.execSQL("ALTER TABLE publicaciones_new RENAME TO publicaciones")

                // Recrear los índices
                db.execSQL("CREATE INDEX IF NOT EXISTS index_publicaciones_categoria_producto ON publicaciones(categoria_producto)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_publicaciones_fecha_modificacion ON publicaciones(fecha_modificacion)")
            }
        }

        // Migración de la versión 2 a la 3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Crear una nueva tabla con la columna nombre_pila
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS publicaciones_new (
                        uuid TEXT NOT NULL PRIMARY KEY,
                        nombre_producto TEXT NOT NULL,
                        categoria_producto TEXT NOT NULL,
                        descripcion_producto TEXT NOT NULL,
                        ubicacion_producto TEXT NOT NULL,
                        precio_producto REAL NOT NULL,
                        imagen_producto TEXT NOT NULL,
                        user_id TEXT NOT NULL,
                        nombre_pila TEXT,
                        fecha_modificacion INTEGER NOT NULL,
                        eliminado_estado INTEGER NOT NULL,
                        sincronizado INTEGER NOT NULL
                    )
                """)

                // Copiar los datos de la tabla antigua a la nueva, usando NULL para nombre_pila
                db.execSQL("""
                    INSERT INTO publicaciones_new (
                        uuid, nombre_producto, categoria_producto, descripcion_producto,
                        ubicacion_producto, precio_producto, imagen_producto, user_id,
                        nombre_pila, fecha_modificacion, eliminado_estado, sincronizado
                    )
                    SELECT 
                        uuid, nombre_producto, categoria_producto, descripcion_producto,
                        ubicacion_producto, precio_producto, imagen_producto, user_id,
                        NULL, fecha_modificacion, eliminado_estado, sincronizado
                    FROM publicaciones
                """)

                // Eliminar la tabla antigua
                db.execSQL("DROP TABLE publicaciones")

                // Renombrar la nueva tabla
                db.execSQL("ALTER TABLE publicaciones_new RENAME TO publicaciones")

                // Recrear los índices
                db.execSQL("CREATE INDEX IF NOT EXISTS index_publicaciones_categoria_producto ON publicaciones(categoria_producto)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_publicaciones_fecha_modificacion ON publicaciones(fecha_modificacion)")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
} 