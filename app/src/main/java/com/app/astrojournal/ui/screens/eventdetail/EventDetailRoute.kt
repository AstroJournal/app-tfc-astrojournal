package com.app.astrojournal.ui.screens.eventdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.app.astrojournal.ui.screens.EventDetailScreen
import com.app.astrojournal.di.AppModule
import com.app.astrojournal.data.model.EventData
import com.app.astrojournal.ui.viewmodels.EventDetailViewModel

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
