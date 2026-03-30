package com.app.astrojournal.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.astrojournal.data.model.AstroEvent
import com.app.astrojournal.di.AppModule
import com.app.astrojournal.ui.screens.CalendarScreen
import com.app.astrojournal.ui.screens.EventDetailScreen
import com.app.astrojournal.ui.screens.HomeScreen
import com.app.astrojournal.ui.screens.LoginScreen
import com.app.astrojournal.ui.screens.RegisterScreen
import com.app.astrojournal.ui.screens.SocialEventsScreen
import com.app.astrojournal.ui.viewmodels.EventDetailViewModel
import com.app.astrojournal.ui.viewmodels.HomeViewModel
import com.app.astrojournal.ui.viewmodels.LoginViewModel
import com.app.astrojournal.ui.viewmodels.RegisterViewModel
import com.app.astrojournal.ui.viewmodels.SocialEventsViewModel
import com.app.astrojournal.ui.theme.AstrojournalTheme

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    // ViewModels that depend on the repository are created via a factory
    private val registerViewModel: RegisterViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                RegisterViewModel(AppModule.userRepository) as T
        }
    }

    private val loginViewModel: LoginViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                LoginViewModel(AppModule.userRepository) as T
        }
    }

    private val socialEventsViewModel: SocialEventsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                SocialEventsViewModel(AppModule.meetupEventRepository) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppModule.init(this)

        setContent {
            AstrojournalTheme {
                var currentScreen by remember { mutableStateOf("login") }
                var selectedEvent by remember { mutableStateOf<AstroEvent?>(null) }

                var initialAstroEventNameForSocial by remember { mutableStateOf<String?>(null) }
                
                when {
                    currentScreen == "login" -> LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = { currentScreen = "home" },
                        onNavigateToRegister = { currentScreen = "register" }
                    )
                    currentScreen == "register" -> RegisterScreen(
                        viewModel = registerViewModel,
                        onRegisterSuccess = { currentScreen = "home" },
                        onNavigateToLogin = { currentScreen = "login" }
                    )
                    currentScreen == "home" -> HomeScreen(
                        viewModel = homeViewModel,
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it },
                        onEventSelected = { event ->
                            selectedEvent = event
                            currentScreen = "eventDetail"
                        }
                    )
                    currentScreen == "calendar" -> CalendarScreen(
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it }
                    )
                    currentScreen.startsWith("social") -> {
                        // Extract query parameter basic approach
                        val routeParams = currentScreen.split("?")
                        if (routeParams.size > 1 && routeParams[1].startsWith("initialEventName=")) {
                            initialAstroEventNameForSocial = routeParams[1].substringAfter("initialEventName=")
                            // Limpiamos currentScreen para que la BottomNav funcione correctamente (marcar social)
                            currentScreen = "social"
                        }
                        
                        SocialEventsScreen(
                            viewModel = socialEventsViewModel,
                            initialAstroEventName = initialAstroEventNameForSocial,
                            currentScreen = "social",
                            onNavigate = { 
                                initialAstroEventNameForSocial = null // Reset on navigate away
                                currentScreen = it 
                            }
                        )
                    }
                    currentScreen == "eventDetail" -> {
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
                            currentScreen = "home"
                        }
                    }
                }
            }
        }
    }
}
