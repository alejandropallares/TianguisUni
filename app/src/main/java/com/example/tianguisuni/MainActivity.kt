package com.example.tianguisuni

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tianguisuni.screens.UIPrincipal
import com.example.tianguisuni.ui.theme.TianguisUniTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TianguisUniTheme {
                UIPrincipal()
            }
        }
    }
}