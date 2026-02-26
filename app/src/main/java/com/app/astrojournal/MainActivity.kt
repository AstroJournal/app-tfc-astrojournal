package com.app.astrojournal.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.app.astrojournal.data.model.AstroEvent
import com.app.astrojournal.di.AppModule
import com.app.astrojournal.ui.screens.CalendarScreen
import com.app.astrojournal.ui.screens.EventDetailScreen
import com.app.astrojournal.ui.screens.HomeScreen
import com.app.astrojournal.ui.viewmodels.EventDetailViewModel
import com.app.astrojournal.ui.viewmodels.HomeViewModel
import com.app.astrojournal.ui.theme.AstrojournalTheme

class MainActivity : ComponentActivity() {

    // ViewModel inyectado desde Activity
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppModule.init(this)

        setContent {
            AstrojournalTheme {
                var currentScreen by remember { mutableStateOf("home") }
                var selectedEvent by remember { mutableStateOf<AstroEvent?>(null) }

                when (currentScreen) {
                    "home" -> HomeScreen(
                        viewModel = homeViewModel,
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it },
                        onEventSelected = { event ->
                            selectedEvent = event
                            currentScreen = "eventDetail"
                        }
                    )
                    "calendar" -> CalendarScreen(
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it }
                    )
                    "eventDetail" -> {
                        val event = selectedEvent
                        if (event != null) {
                            val eventDetailViewModel = remember(event.timestamp) {
                                EventDetailViewModel(AppModule.collectibleRepository)
                            }
                            EventDetailScreen(
                                event = event,
                                viewModel = eventDetailViewModel,
                                currentScreen = currentScreen,
                                onNavigate = { destination ->
                                    currentScreen = destination
                                },
                                onEventSelected = { newEvent ->
                                    selectedEvent = newEvent
                                }
                            )
                        } else {
                            // Fallback: volver a home si no hay evento seleccionado
                            currentScreen = "home"
                        }
                    }
                }
            }
        }
    }
}
