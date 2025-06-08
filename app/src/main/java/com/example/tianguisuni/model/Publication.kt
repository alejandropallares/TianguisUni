package com.example.tianguisuni.model

import android.net.Uri

data class Publication(
    val id: String = "",
    val name: String,
    val category: String,
    val description: String,
    val location: String,
    val price: Double,
    val imageUri: Uri?,
    val userId: String,
    val createdAt: Long = System.currentTimeMillis()
) 