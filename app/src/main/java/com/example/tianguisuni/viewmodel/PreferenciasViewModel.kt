package com.example.tianguisuni.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenciasViewModel(private val context: Context) : ViewModel() {
    companion object {
        private val RED_COLOR = intPreferencesKey("red_color")
        private val GREEN_COLOR = intPreferencesKey("green_color")
        private val BLUE_COLOR = intPreferencesKey("blue_color")
    }

    val colorPreferences: Flow<Triple<Int, Int, Int>> = context.dataStore.data
        .map { preferences ->
            Triple(
                preferences[RED_COLOR] ?: 162,
                preferences[GREEN_COLOR] ?: 208,
                preferences[BLUE_COLOR] ?: 115
            )
        }

    fun saveColorPreferences(red: Int, green: Int, blue: Int) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[RED_COLOR] = red
                preferences[GREEN_COLOR] = green
                preferences[BLUE_COLOR] = blue
            }
        }
    }
}

class PreferenciasViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreferenciasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PreferenciasViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 