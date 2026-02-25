package com.app.astrojournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.app.astrojournal.ui.screens.HomeScreen
import com.app.astrojournal.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {

    // ViewModel inyectado desde Activity
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Pasamos el ViewModel a HomeScreen
            HomeScreen(viewModel = homeViewModel)
        }
    }
}
