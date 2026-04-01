package com.app.astrojournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.astrojournal.R
import com.app.astrojournal.data.model.AstroEvent
import com.app.astrojournal.data.model.VisibilityUi
import com.app.astrojournal.data.repository.SpaceRepository
import com.app.astrojournal.ui.components.AstroBottomNavigation
import com.app.astrojournal.ui.viewmodels.AgendaFilter
import com.app.astrojournal.ui.viewmodels.EventDetailViewModel
import com.app.astrojournal.ui.viewmodels.RemoteUiState
import com.astrojournal.shared.data.db.Collectible
import kotlinx.coroutines.delay
import java.time.LocalDate

@Composable
fun EventDetailScreen(
    event: AstroEvent,
    viewModel: EventDetailViewModel,
    currentScreen: String = "eventDetail",
    onNavigate: (String) -> Unit = {},
    onEventSelected: (AstroEvent) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val now = System.currentTimeMillis()
    val eventId = event.timestamp
    val isFuture = event.timestamp > now

    var noteInput by remember { mutableStateOf("") }
    var visibilityExpanded by remember { mutableStateOf(false) }
    var eventToRemoveFromAgenda by remember { mutableStateOf<Collectible?>(null) }
    var itemToMarkObserved by remember { mutableStateOf<com.astrojournal.shared.data.db.Collectible?>(null) }
    var currentScreenItemToMarkObserved by remember { mutableStateOf<Boolean>(false) }

    val visibilityState = produceState<RemoteUiState<VisibilityUi>>(
        initialValue = RemoteUiState.Loading,
        key1 = Unit
    ) {
        val repository = SpaceRepository()
        value = runCatching { repository.getVisibilityWithFallback(LocalDate.now()) }
            .fold(
                onSuccess = { RemoteUiState.Success(it) },
                onFailure = { RemoteUiState.Error("Information not available") }
            )
    }.value

    val allAgended = viewModel.collectibles.filter { it.agended == 1L }
    val futureAgended = allAgended.filter { it.eventId > now }
    val pastAgended = allAgended.filter { it.eventId <= now && it.observed == 0L }

    LaunchedEffect(viewModel.collectibles) {
        viewModel.updateEventStatus(eventId)
        noteInput = viewModel.currentEventNote
    }

    if (eventToRemoveFromAgenda != null) {
        AlertDialog(
            onDismissRequest = { eventToRemoveFromAgenda = null },
            title = { Text("Remove from agenda", color = Color.White) },
            text = { Text("Are you sure you want to remove this event from your agenda?", color = Color.White) },
            confirmButton = {
                TextButton(onClick = {
                    val item = eventToRemoveFromAgenda!!
                    viewModel.saveFullEventState(
                        eventId = item.eventId,
                        eventName = item.eventName,
                        date = item.observationDate,
                        note = item.notes ?: "",
                        agended = false,
                        observed = item.observed == 1L
                    )
                    eventToRemoveFromAgenda = null
                }) {
                    Text("Remove", color = Color(0xFFFCA5A5))
                }
            },
            dismissButton = {
                TextButton(onClick = { eventToRemoveFromAgenda = null }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }

    if (itemToMarkObserved != null || currentScreenItemToMarkObserved) {
        AlertDialog(
            onDismissRequest = { 
                itemToMarkObserved = null 
                currentScreenItemToMarkObserved = false
            },
            title = { Text("Mark as observed", color = Color.White) },
            text = { Text("Are you sure you want to mark this event as observed?", color = Color.White) },
            confirmButton = {
                TextButton(onClick = {
                    if (itemToMarkObserved != null) {
                        val item = itemToMarkObserved!!
                        viewModel.saveFullEventState(
                            eventId = item.eventId,
                            eventName = item.eventName,
                            date = item.observationDate,
                            note = item.notes ?: "",
                            agended = item.agended == 1L,
                            observed = true
                        )
                        itemToMarkObserved = null
                    } else if (currentScreenItemToMarkObserved) {
                        viewModel.saveFullEventState(
                            eventId = eventId,
                            eventName = event.name,
                            date = event.date,
                            note = noteInput,
                            agended = viewModel.isEventAgended,
                            observed = true
                        )
                        currentScreenItemToMarkObserved = false
                    }
                }) {
                    Text("Confirm", color = Color(0xFFA5B4FC))
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    itemToMarkObserved = null 
                    currentScreenItemToMarkObserved = false
                }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }

    Scaffold(
        topBar = { com.app.astrojournal.ui.components.AstroTopBar(onProfileClick = { onNavigate("profile") }) },
        bottomBar = { AstroBottomNavigation(currentScreen = currentScreen, onNavigate = onNavigate) },
        containerColor = Color.Transparent,
        contentColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.stary_night_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Image(
                painter = painterResource(id = getEventBackgroundImage(event.name)),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().alpha(0.24f),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .testTag("event_detail_screen")
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding() + 24.dp))

                EventDetailVisibilitySection(
                    state = visibilityState,
                    expanded = visibilityExpanded,
                    onToggleExpanded = { visibilityExpanded = !visibilityExpanded }
                )

                Spacer(modifier = Modifier.height(16.dp))

                EventEditorCard(
                        event = event,
                        localDateTime = event.date,
                        countdownText = getCountdownText(event.timestamp),
                        noteInput = noteInput,
                        currentNote = viewModel.currentEventNote,
                        isFuture = isFuture,
                        isObserved = viewModel.isEventObserved,
                        isAgended = viewModel.isEventAgended,
                        onNoteChange = { noteInput = it },
                        onSaveNote = {
                            viewModel.saveFullEventState(
                                eventId = eventId,
                                eventName = event.name,
                                date = event.date,
                                note = noteInput,
                                agended = viewModel.isEventAgended,
                                observed = viewModel.isEventObserved
                            )
                            focusManager.clearFocus()
                        },
                        onToggleObserved = { checked ->
                            if (checked) {
                                currentScreenItemToMarkObserved = true
                            } else {
                                viewModel.saveFullEventState(
                                    eventId = eventId,
                                    eventName = event.name,
                                    date = event.date,
                                    note = noteInput,
                                    agended = viewModel.isEventAgended,
                                    observed = false
                                )
                            }
                        },
                        onPrimaryAction = {
                            val newAgended = if (isFuture) !viewModel.isEventAgended else viewModel.isEventAgended
                            val newObserved = if (!isFuture) !viewModel.isEventObserved else viewModel.isEventObserved
                            
                            if (!isFuture && newObserved && !viewModel.isEventObserved) {
                                currentScreenItemToMarkObserved = true
                            } else {
                                viewModel.saveFullEventState(
                                    eventId = eventId,
                                    eventName = event.name,
                                    date = event.date,
                                    note = noteInput,
                                    agended = newAgended,
                                    observed = newObserved
                                )
                            }
                        },
                        onDeleteNote = {
                            viewModel.saveFullEventState(
                                eventId = eventId,
                                eventName = event.name,
                                date = event.date,
                                note = "",
                                agended = viewModel.isEventAgended,
                                observed = viewModel.isEventObserved
                            )
                            noteInput = ""
                        },
                        onMakeMeetupClick = { onNavigate("social?initialEventName=${event.name}") }
                    )

                Spacer(modifier = Modifier.height(16.dp))

                if (futureAgended.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AgendaListCard(
                        title = "My Agenda",
                        titleColor = Color(0xFF818CF8),
                        items = futureAgended.sortedBy { it.eventId },
                        onSelectEvent = { onEventSelected(it.toAstroEvent()) },
                        onToggleObserved = { item, checked ->
                            viewModel.saveFullEventState(
                                eventId = item.eventId,
                                eventName = item.eventName,
                                date = item.observationDate,
                                note = item.notes ?: "",
                                agended = true,
                                observed = checked
                            )
                        },
                        onRemove = { eventToRemoveFromAgenda = it }
                    )
                }

                if (pastAgended.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AgendaListCard(
                        title = "Past Events",
                        titleColor = Color(0xFF94A3B8), // Muted color
                        items = pastAgended.sortedByDescending { it.eventId },
                        onSelectEvent = { onEventSelected(it.toAstroEvent()) },
                        onToggleObserved = { item, checked ->
                            if (checked) {
                                itemToMarkObserved = item
                            } else {
                                viewModel.saveFullEventState(
                                    eventId = item.eventId,
                                    eventName = item.eventName,
                                    date = item.observationDate,
                                    note = item.notes ?: "",
                                    agended = true,
                                    observed = false
                                )
                            }
                        },
                        onRemove = { eventToRemoveFromAgenda = it }
                    )
                }

                Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding() + 24.dp))
            }
        }
    }
}

private fun Collectible.toAstroEvent(): AstroEvent {
    return AstroEvent(
        name = eventName,
        date = observationDate,
        description = "",
        timestamp = eventId,
        type = com.app.astrojournal.data.model.EventType.OTHER
    )
}
