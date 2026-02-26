package com.app.astrojournal.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.app.astrojournal.di.AppModule
import com.app.astrojournal.ui.screens.CalendarScreen
import com.app.astrojournal.ui.screens.HomeScreen
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

                when (currentScreen) {
                    "home" -> HomeScreen(
                        viewModel = homeViewModel,
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it }
                    )
                    "calendar" -> CalendarScreen(
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it }
                    )
                }
            }
        }
    }
}
