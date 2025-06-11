package com.example.tianguisuni.data.repository

import com.example.tianguisuni.data.database.DatabaseProvider
import com.example.tianguisuni.data.entities.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsuarioRepository(private val databaseProvider: DatabaseProvider) {
    private val usuarioDao = databaseProvider.usuarioDao

    suspend fun insertUsuario(usuario: Usuario) = withContext(Dispatchers.IO) {
        usuarioDao.insertUsuario(usuario)
    }

    suspend fun getUsuario(id: String) = withContext(Dispatchers.IO) {
        usuarioDao.getUsuario(id)
    }

    suspend fun getUsuarioByUsername(username: String) = withContext(Dispatchers.IO) {
        usuarioDao.getUsuarioByUsername(username)
    }

    suspend fun getAllUsuarios() = withContext(Dispatchers.IO) {
        usuarioDao.getAllUsuarios()
    }

    suspend fun deleteUsuario(usuario: Usuario) = withContext(Dispatchers.IO) {
        usuarioDao.deleteUsuario(usuario)
    }

    suspend fun deleteAllUsuarios() = withContext(Dispatchers.IO) {
        usuarioDao.deleteAllUsuarios()
    }

    suspend fun updateUsuario(usuario: Usuario) = withContext(Dispatchers.IO) {
        usuarioDao.updateUsuario(usuario)
    }
} 