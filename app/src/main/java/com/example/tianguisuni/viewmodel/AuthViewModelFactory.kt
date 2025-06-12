package com.example.tianguisuni.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val viewModel = AuthViewModel()
            viewModel.initialize(context)
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory {
            return AuthViewModelFactory(context)
        }
    }
} 