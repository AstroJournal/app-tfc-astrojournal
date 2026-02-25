package com.app.astrojournal.eventdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.app.astrojournal.ui.screens.EventDetailScreen
import com.app.astrojournal.di.AppModule
import com.app.astrojournal.model.EventData

@Composable
fun EventDetailRoute(eventId: Long) {

    val viewModel = remember(eventId) {
        EventDetailViewModel(AppModule.collectibleRepository)
    }

    val event = EventData.events.find { it.id == eventId }
        ?: return

    EventDetailScreen(
        event = event,
        viewModel = viewModel
    )
}
