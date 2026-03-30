package com.app.astrojournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.astrojournal.R
import com.app.astrojournal.data.model.AstroEvent
import com.app.astrojournal.ui.components.AstroBottomNavigation
import com.app.astrojournal.ui.viewmodels.EventDetailViewModel

@Composable
fun EventDetailScreen(
    event: AstroEvent,
    viewModel: EventDetailViewModel,
    currentScreen: String = "eventDetail",
    onNavigate: (String) -> Unit = {},
    onEventSelected: (AstroEvent) -> Unit = {}
) {
    val collectibles = viewModel.collectibles
    var noteInput by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val now = System.currentTimeMillis()

    var eventToRemoveFromAgenda by remember { mutableStateOf<com.astrojournal.shared.data.db.Collectible?>(null) }
    var eventToMarkAsViewed by remember { mutableStateOf<com.astrojournal.shared.data.db.Collectible?>(null) }

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

    if (eventToMarkAsViewed != null) {
        AlertDialog(
            onDismissRequest = { eventToMarkAsViewed = null },
            title = { Text("Mark as viewed", color = Color.White) },
            text = { Text("Do you want to mark this past event as observed?", color = Color.White) },
            confirmButton = {
                TextButton(onClick = {
                    val item = eventToMarkAsViewed!!
                    viewModel.saveFullEventState(
                        eventId = item.eventId,
                        eventName = item.eventName,
                        date = item.observationDate,
                        note = item.notes ?: "",
                        agended = false,
                        observed = true
                    )
                    eventToMarkAsViewed = null
                }) {
                    Text("Mark viewed", color = Color(0xFF6366F1))
                }
            },
            dismissButton = {
                TextButton(onClick = { eventToMarkAsViewed = null }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }

    // Use the event timestamp as a stable ID
    val eventId = event.timestamp
    val isFuture = event.timestamp > System.currentTimeMillis()

    // Keep the status in sync and initialize note input
    LaunchedEffect(collectibles) {
        viewModel.updateEventStatus(eventId)
        noteInput = viewModel.currentEventNote
    }

    Scaffold(
        topBar = { com.app.astrojournal.ui.components.AstroTopBar(onProfileClick = { onNavigate("profile") }) },
        bottomBar = {
            AstroBottomNavigation(
                currentScreen = currentScreen,
                onNavigate = onNavigate
            )
        },
        containerColor = Color.Transparent,
        contentColor = Color.White
    ) { innerPadding ->

        // Full screen background
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.stary_night_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Panel 1: Event Info, Integrated Notes & Action
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.7f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = event.date,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8)
                    )
                    
                    if (event.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    androidx.compose.material3.HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Personal Notes",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF6366F1)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { 
                            noteInput = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Add an optional note...", color = Color(0xFF475569)) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6366F1),
                            unfocusedBorderColor = Color(0xFF334155),
                            cursorColor = Color(0xFF6366F1),
                            focusedContainerColor = Color.Black.copy(alpha = 0.2f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.1f)
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            viewModel.saveFullEventState(
                                eventId = eventId,
                                eventName = event.name,
                                date = event.date,
                                note = noteInput,
                                agended = viewModel.isEventAgended,
                                observed = viewModel.isEventObserved
                            )
                            focusManager.clearFocus()
                        }),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (noteInput != viewModel.currentEventNote) {
                        Spacer(modifier = Modifier.height(8.dp))
                        androidx.compose.material3.TextButton(
                            onClick = {
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
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                androidx.compose.material3.Icon(
                                    imageVector = if (viewModel.currentEventNote.isNotBlank()) Icons.Filled.Check else Icons.Filled.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFF6366F1)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (viewModel.currentEventNote.isNotBlank()) "Save changes" else "Save note",
                                    color = Color(0xFF6366F1),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Integrated Action Button
                    val actionLabel = if (isFuture) {
                        if (viewModel.isEventAgended) "Remove from agenda" else "Add to agenda"
                    } else {
                        if (viewModel.isEventObserved) "Unmark as observed" else "Mark as observed"
                    }
                    
                    val buttonColor = if (isFuture) {
                        if (viewModel.isEventAgended) Color(0xFF451A1A) else Color(0xFF6366F1)
                    } else {
                        if (viewModel.isEventObserved) Color(0xFF334155) else Color(0xFF6366F1)
                    }
                    
                    val contentColor = if (isFuture && viewModel.isEventAgended) Color(0xFFFCA5A5) else Color.White

                    Button(
                        onClick = {
                            val newAgended = if (isFuture) !viewModel.isEventAgended else viewModel.isEventAgended
                            val newObserved = if (!isFuture) !viewModel.isEventObserved else viewModel.isEventObserved
                            
                            viewModel.saveFullEventState(
                                eventId = eventId,
                                eventName = event.name,
                                date = event.date,
                                note = noteInput,
                                agended = newAgended,
                                observed = newObserved
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = contentColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isFuture) {
                                if (viewModel.isEventAgended) {
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = contentColor
                                    )
                                } else {
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Filled.DateRange,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else {
                                if (viewModel.isEventObserved) {
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = actionLabel, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Connect to Social Events Feature
                    Button(
                        onClick = {
                            // Enviar el nombre del evento como parámetro en la navegación a social
                            onNavigate("social?initialEventName=${event.name}")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E293B), // Background dark
                            contentColor = Color(0xFFA5B4FC)    // Indigo pale
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Filled.People,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Create meetup for this event", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Panel 2: Mi Agenda (Future)
                val allAgended = collectibles.filter { it.agended == 1L }
                val futureAgended = allAgended.filter { it.eventId > now }
                val pastAgended = allAgended.filter { it.eventId <= now }

                if (futureAgended.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF1E293B).copy(alpha = 0.7f))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                            .padding(20.dp)
                    ) {
                        Text("My Agenda", style = MaterialTheme.typography.titleMedium, color = Color(0xFF818CF8), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))

                        futureAgended.forEach { agendaItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .clickable {
                                        onEventSelected(AstroEvent(
                                            name = agendaItem.eventName,
                                            date = agendaItem.observationDate,
                                            description = "", 
                                            timestamp = agendaItem.eventId,
                                            type = com.app.astrojournal.data.model.EventType.OTHER
                                        ))
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF6366F1).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🔭", fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = agendaItem.eventName, color = Color.White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Text(text = agendaItem.observationDate, color = Color(0xFF94A3B8), style = MaterialTheme.typography.labelSmall)
                                    
                                    if (!agendaItem.notes.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = agendaItem.notes!!,
                                            color = Color(0xFFCBD5E1),
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { eventToRemoveFromAgenda = agendaItem },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Remove",
                                        tint = Color(0xFFFCA5A5),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (pastAgended.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF1E293B).copy(alpha = 0.7f))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                            .padding(20.dp)
                    ) {
                        Text("Past Events", style = MaterialTheme.typography.titleMedium, color = Color(0xFFA5B4FC), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))

                        pastAgended.forEach { agendaItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .clickable {
                                        onEventSelected(AstroEvent(
                                            name = agendaItem.eventName,
                                            date = agendaItem.observationDate,
                                            description = "", 
                                            timestamp = agendaItem.eventId,
                                            type = com.app.astrojournal.data.model.EventType.OTHER
                                        ))
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF475569).copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("⏸️", fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = agendaItem.eventName, color = Color(0xFFCBD5E1), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Text(text = agendaItem.observationDate, color = Color(0xFF64748B), style = MaterialTheme.typography.labelSmall)
                                }
                                IconButton(
                                    onClick = { eventToMarkAsViewed = agendaItem },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Visibility,
                                        contentDescription = "Mark as viewed",
                                        tint = Color(0xFF6366F1),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { eventToRemoveFromAgenda = agendaItem },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Remove",
                                        tint = Color(0xFFFCA5A5),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}


