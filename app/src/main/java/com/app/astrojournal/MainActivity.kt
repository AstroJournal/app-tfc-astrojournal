package com.app.astrojournal.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
<<<<<<< HEAD
import com.app.astrojournal.di.AppModule
import com.app.astrojournal.eventdetail.EventDetailRoute
import com.app.astrojournal.ui.theme.AstrojournalTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppModule.init(this)

        setContent {
            AstrojournalTheme {
                EventDetailRoute(eventId = 1L)   // ← TU EVENTO
            }
=======
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
>>>>>>> bc821a1e650413544953f20884ac65c28fb11123
        }
    }
}
