package com.app.astrojournal.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.app.astrojournal.data.model.AstroEvent
import com.app.astrojournal.di.AppModule
import com.app.astrojournal.ui.screens.CalendarScreen
import com.app.astrojournal.ui.screens.EventDetailScreen
import com.app.astrojournal.ui.screens.EventOfTheDayScreen
import com.app.astrojournal.ui.screens.HomeScreen
import com.app.astrojournal.ui.screens.LoginScreen
import com.app.astrojournal.ui.screens.RegisterScreen
import com.app.astrojournal.ui.screens.ObservedEventsScreen
import com.app.astrojournal.ui.screens.SocialEventsScreen
import com.app.astrojournal.ui.viewmodels.CalendarViewModel
import com.app.astrojournal.ui.viewmodels.EventDetailViewModel
import com.app.astrojournal.ui.viewmodels.EventOfTheDayViewModel
import com.app.astrojournal.ui.viewmodels.HomeViewModel
import com.app.astrojournal.ui.viewmodels.LoginViewModel
import com.app.astrojournal.ui.viewmodels.ObservedEventsViewModel
import com.app.astrojournal.ui.viewmodels.RegisterViewModel
import com.app.astrojournal.ui.viewmodels.SocialEventsViewModel
import com.app.astrojournal.ui.theme.AstrojournalTheme

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val eventOfTheDayViewModel: EventOfTheDayViewModel by viewModels()

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

    private val calendarViewModel: CalendarViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                CalendarViewModel(AppModule.collectibleRepository) as T
        }
    }

    private val observedEventsViewModel: ObservedEventsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ObservedEventsViewModel(AppModule.collectibleRepository) as T
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
                
                val loggedInUser by loginViewModel.loggedInUser.collectAsState()
                val plainPassword by loginViewModel.plainPassword.collectAsState()

                fun navigateTo(destination: String) {
                    if (destination == "eventDetail") {
                        if (selectedEvent == null) {
                            selectedEvent = homeViewModel.upcomingEvents.value.firstOrNull()
                        }
                        currentScreen = if (selectedEvent != null) "eventDetail" else "home"
                    } else {
                        currentScreen = destination
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
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
                        eventOfTheDayViewModel = eventOfTheDayViewModel,
                        currentScreen = currentScreen,
                        onNavigate = { navigateTo(it) },
                        onEventSelected = { event ->
                            selectedEvent = event
                            currentScreen = "eventDetail"
                        }
                    )
                    currentScreen == "calendar" -> CalendarScreen(
                        viewModel = calendarViewModel,
                        currentScreen = currentScreen,
                        onNavigate = { navigateTo(it) }
                    )
                    currentScreen == "eventOfTheDay" -> EventOfTheDayScreen(
                        viewModel = eventOfTheDayViewModel,
                        currentScreen = currentScreen,
                        onNavigate = { navigateTo(it) }
                    )
                    currentScreen == "observed" -> ObservedEventsScreen(
                        viewModel = observedEventsViewModel,
                        currentScreen = currentScreen,
                        onNavigate = { navigateTo(it) }
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
                                navigateTo(it) 
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
                                    navigateTo(destination)
                                },
                                onEventSelected = { newEvent ->
                                    selectedEvent = newEvent
                                }
                            )
                        } else {
                            currentScreen = "home"
                        }
                    }
                    currentScreen == "profile" -> com.app.astrojournal.ui.screens.ProfileScreen(
                        viewModel = observedEventsViewModel,
                        username = loggedInUser?.username ?: "Astro User",
                        email = loggedInUser?.email ?: "usuario@ejemplo.com",
                        password = plainPassword, // <--- Aquí pasamos la contraseña real
                        onUpdatePassword = { loginViewModel.updatePassword(it) },
                        currentScreen = currentScreen,
                        onNavigate = { navigateTo(it) }
                    )
                }

            } // Box
            } // AstrojournalTheme
        } // setContent
    } // onCreate
} // class MainActivity
