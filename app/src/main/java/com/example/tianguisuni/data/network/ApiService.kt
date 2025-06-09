package com.example.tianguisuni.data.network

import com.example.tianguisuni.data.entities.Publicacion
import com.example.tianguisuni.data.entities.Usuario
import com.example.tianguisuni.data.network.models.LoginRequest
import com.example.tianguisuni.data.network.models.LoginResponse
import com.example.tianguisuni.data.network.models.SyncResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api.php/login")
    suspend fun login(@Body credentials: LoginRequest): LoginResponse

    @POST("api.php/register")
    suspend fun register(@Body usuario: Usuario): Response<Unit>

    @GET("api.php/posts")
    suspend fun getPublicaciones(): List<Publicacion>

    @POST("api.php/sync")
    suspend fun syncPublicaciones(@Body publicaciones: List<Publicacion>): SyncResponse

    @POST("api.php/posts")
    suspend fun createPublicacion(@Body publicacion: Publicacion): Response<Unit>

    @PUT("api.php/posts/{uuid}")
    suspend fun updatePublicacion(
        @Path("uuid") uuid: String,
        @Body publicacion: Publicacion
    ): Response<Unit>

    @DELETE("api.php/posts/{uuid}")
    suspend fun deletePublicacion(
        @Path("uuid") uuid: String,
        @Query("user_id") userId: String
    ): Response<Unit>
} 