package com.example.tianguisuni.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PublicacionesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PublicacionesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PublicacionesViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 