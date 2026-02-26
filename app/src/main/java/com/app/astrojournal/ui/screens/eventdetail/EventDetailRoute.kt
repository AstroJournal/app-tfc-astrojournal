package com.app.astrojournal.ui.screens.eventdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.app.astrojournal.ui.screens.EventDetailScreen
import com.app.astrojournal.di.AppModule
import com.app.astrojournal.data.model.AstroEvent
import com.app.astrojournal.ui.viewmodels.EventDetailViewModel

/**
 * Route composable for EventDetailScreen.
 * Used when navigating via an AstroEvent directly.
 */
@Composable
fun EventDetailRoute(
    event: AstroEvent,
    currentScreen: String = "eventDetail",
    onNavigate: (String) -> Unit = {}
) {
    val viewModel = remember(event.timestamp) {
        EventDetailViewModel(AppModule.collectibleRepository)
    }

    EventDetailScreen(
        event = event,
        viewModel = viewModel,
        currentScreen = currentScreen,
        onNavigate = onNavigate
    )
}
