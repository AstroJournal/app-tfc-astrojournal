package com.app.astrojournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.astrojournal.R
import com.app.astrojournal.data.model.AstroEvent
import com.app.astrojournal.ui.components.AstroBottomNavigation
import com.app.astrojournal.ui.viewmodels.AgendaFilter
import com.app.astrojournal.ui.viewmodels.EventDetailViewModel
import com.astrojournal.shared.data.db.Collectible
import kotlinx.coroutines.delay

@Composable
fun EventDetailScreen(
    event: AstroEvent,
    viewModel: EventDetailViewModel,
    currentScreen: String = "eventDetail",
    onNavigate: (String) -> Unit = {},
    onEventSelected: (AstroEvent) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val eventId = event.timestamp
    val isFuture = event.timestamp > System.currentTimeMillis()
    val isAlreadyInAgenda = isFuture && viewModel.isEventAgended

    var noteInput by remember { mutableStateOf("") }
    var showDeleteNoteDialog by remember { mutableStateOf(false) }
    var showAgendaAddedMessage by remember { mutableStateOf(false) }
    var previousAgendedState by remember { mutableStateOf<Boolean?>(null) }
    var forceShowEditor by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<Collectible?>(null) }

    LaunchedEffect(eventId) {
        showAgendaAddedMessage = false
        previousAgendedState = null
        forceShowEditor = false
        viewModel.loadEvent(eventId)
    }

    LaunchedEffect(viewModel.currentEventNote) {
        noteInput = viewModel.currentEventNote
    }

    LaunchedEffect(viewModel.isEventAgended, eventId, isFuture) {
        val prev = previousAgendedState
        if (prev == false && viewModel.isEventAgended && isFuture) {
            showAgendaAddedMessage = true
            delay(2600)
            showAgendaAddedMessage = false
        }
        previousAgendedState = viewModel.isEventAgended
    }

    val agendedEvents = viewModel.collectibles.filter { it.agended == 1L }.sortedBy { it.eventId }
    val filteredEvents = when (viewModel.agendaFilter) {
        AgendaFilter.ALL -> agendedEvents
        AgendaFilter.PENDING -> agendedEvents.filter { it.observed == 0L }
        AgendaFilter.OBSERVED -> agendedEvents.filter { it.observed == 1L }
    }

    fun saveCurrentEvent(note: String = noteInput, agended: Boolean = viewModel.isEventAgended, observed: Boolean = viewModel.isEventObserved) {
        viewModel.saveFullEventState(
            eventId = eventId,
            eventName = event.name,
            date = event.date,
            note = note,
            agended = agended,
            observed = observed
        )
    }

    Scaffold(
        bottomBar = { AstroBottomNavigation(currentScreen = currentScreen, onNavigate = onNavigate) },
        containerColor = Color.Transparent,
        contentColor = Color.White
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.stary_night_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                if (!isAlreadyInAgenda || forceShowEditor) {
                    EventEditorCard(
                        event = event,
                        localDateTime = formatLocalDateTime(eventId),
                        countdownText = getCountdownText(eventId),
                        noteInput = noteInput,
                        currentNote = viewModel.currentEventNote,
                        isFuture = isFuture,
                        isObserved = viewModel.isEventObserved,
                        isAgended = viewModel.isEventAgended,
                        onNoteChange = { noteInput = it },
                        onSaveNote = {
                            saveCurrentEvent()
                            forceShowEditor = false
                            focusManager.clearFocus()
                        },
                        onPrimaryAction = {
                            val newAgended = if (isFuture) true else viewModel.isEventAgended
                            val newObserved = if (!isFuture) !viewModel.isEventObserved else viewModel.isEventObserved
                            saveCurrentEvent(agended = newAgended, observed = newObserved)
                            if (isFuture && viewModel.isEventAgended) forceShowEditor = false
                        },
                        onDeleteNote = { showDeleteNoteDialog = true }
                    )
                }

                if (showAgendaAddedMessage) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Evento anadido correctamente",
                        color = Indigo300,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (agendedEvents.isNotEmpty()) {
                    AgendaSection(
                        selectedFilter = viewModel.agendaFilter,
                        filteredEvents = filteredEvents,
                        currentEventId = eventId,
                        onFilterSelected = { viewModel.updateAgendaFilter(it) },
                        onSelectEvent = { item ->
                            onEventSelected(
                                AstroEvent(
                                    name = item.eventName,
                                    date = item.observationDate,
                                    description = "",
                                    timestamp = item.eventId,
                                    type = inferEventType(item.eventName)
                                )
                            )
                        },
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
                        onEdit = { item ->
                            if (item.eventId == eventId) {
                                noteInput = item.notes ?: ""
                                forceShowEditor = true
                            } else {
                                onEventSelected(
                                    AstroEvent(
                                        name = item.eventName,
                                        date = item.observationDate,
                                        description = "",
                                        timestamp = item.eventId,
                                        type = inferEventType(item.eventName)
                                    )
                                )
                            }
                        },
                        onDelete = { eventToDelete = it }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showDeleteNoteDialog) {
        DeleteNoteDialog(
            onConfirm = {
                showDeleteNoteDialog = false
                saveCurrentEvent(note = "")
            },
            onDismiss = { showDeleteNoteDialog = false }
        )
    }

    eventToDelete?.let { target ->
        DeleteAgendaItemDialog(
            item = target,
            onConfirm = {
                viewModel.saveFullEventState(
                    eventId = target.eventId,
                    eventName = target.eventName,
                    date = target.observationDate,
                    note = "",
                    agended = false,
                    observed = false
                )
                eventToDelete = null
            },
            onDismiss = { eventToDelete = null }
        )
    }
}
