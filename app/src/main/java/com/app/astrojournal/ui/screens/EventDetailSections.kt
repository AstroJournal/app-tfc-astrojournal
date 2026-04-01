package com.app.astrojournal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.astrojournal.data.model.AstroEvent
import com.app.astrojournal.data.model.VisibilityUi
import com.app.astrojournal.ui.viewmodels.AgendaFilter
import com.app.astrojournal.ui.viewmodels.RemoteUiState
import com.astrojournal.shared.data.db.Collectible

@Composable
fun EventEditorCard(
    event: AstroEvent,
    localDateTime: String,
    countdownText: String,
    noteInput: String,
    currentNote: String,
    isFuture: Boolean,
    isObserved: Boolean,
    isAgended: Boolean,
    onNoteChange: (String) -> Unit,
    onSaveNote: () -> Unit,
    onToggleObserved: (Boolean) -> Unit,
    onPrimaryAction: () -> Unit,
    onDeleteNote: () -> Unit,
    onMakeMeetupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GlassPanelBg)
            .border(1.dp, White10, RoundedCornerShape(24.dp))
            .padding(20.dp)
            .testTag("event_detail_success")
    ) {
        Text(
            text = event.name,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("event_detail_title")
        )
        Text(localDateTime, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF94A3B8))
        Text(countdownText, style = MaterialTheme.typography.labelMedium, color = Color(0xFF818CF8))

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Personal Notes", style = MaterialTheme.typography.labelMedium, color = Color(0xFF6366F1))
            if (currentNote.isNotBlank()) {
                TextButton(onClick = onDeleteNote) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Note",
                        tint = Color(0xFFFCA5A5),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear Note", color = Color(0xFFFCA5A5))
                }
            }
        }

        OutlinedTextField(
            value = noteInput,
            onValueChange = onNoteChange,
            modifier = Modifier.fillMaxWidth().testTag("event_note_input"),
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
            keyboardActions = KeyboardActions(onDone = { onSaveNote() }),
            shape = RoundedCornerShape(12.dp)
        )

        if (noteInput != currentNote) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onSaveNote,
                modifier = Modifier.align(Alignment.End).testTag("event_save_note_button")
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF6366F1)
                )
                Text("Save changes", color = Color(0xFF6366F1))
            }
        }

        if (!isFuture) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mark as observed", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.bodyMedium)
                Checkbox(
                    checked = isObserved,
                    onCheckedChange = onToggleObserved
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val actionLabel = if (isFuture) {
            if (isAgended) "Save changes" else "Add to My Agenda"
        } else {
            if (isObserved) "Unmark observed" else "Mark as observed"
        }
        val buttonColor = if (isFuture) Color(0xFF6366F1) else if (isObserved) Color(0xFF334155) else Color(0xFF10B981)

        Button(
            onClick = onPrimaryAction,
            modifier = Modifier.fillMaxWidth().testTag("event_toggle_status_button"),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Icon(
                    imageVector = if (isFuture) Icons.Filled.DateRange else if (isObserved) Icons.Filled.Refresh else Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = actionLabel, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onMakeMeetupClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E293B),
                contentColor = Color(0xFFA5B4FC)
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.People, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Create meetup", fontWeight = FontWeight.Bold)
            }
        }
    }
}



@Composable
fun EventDetailVisibilitySection(
    state: RemoteUiState<VisibilityUi>,
    expanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GlassPanelBg)
            .border(1.dp, White10, RoundedCornerShape(24.dp))
            .clickable { onToggleExpanded() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Visibility", color = Color(0xFFA5B4FC), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = Color(0xFF818CF8)
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .padding(12.dp)
            ) {
                when (state) {
                    is RemoteUiState.Loading -> Text("Calculating...", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.bodyMedium)
                    is RemoteUiState.Error -> Text("Information not available", color = Color(0xFF94A3B8), style = MaterialTheme.typography.bodyMedium)
                    is RemoteUiState.Success -> {
                        val data = state.data
                        Column {
                            Text(
                                if (data.isObservable) "Observable" else "Limited visibility",
                                color = Color(0xFF818CF8),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Window: ${data.window}", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(data.message, color = Color(0xFFCBD5E1), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventAddedMessageCard(eventName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GlassPanelBg)
            .border(1.dp, White10, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "Event added to your agenda",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFA5B4FC),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "$eventName now appears below in My Agenda.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFCBD5E1)
        )
    }
}

@Composable
fun EventMeetupButton(eventName: String, onNavigate: (String) -> Unit) {
    Button(
        onClick = { onNavigate("social?initialEventName=$eventName") },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1E293B),
            contentColor = Color(0xFFA5B4FC)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.People, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Create meetup for this event", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AgendaListCard(
    title: String,
    titleColor: Color,
    items: List<Collectible>,
    onSelectEvent: (Collectible) -> Unit,
    onToggleObserved: (Collectible, Boolean) -> Unit,
    onRemove: (Collectible) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GlassPanelBg)
            .border(1.dp, White10, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = titleColor, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        items.forEach { item ->
            AgendaListRow(
                item = item,
                onSelect = { onSelectEvent(item) },
                onToggleObserved = { checked -> onToggleObserved(item, checked) },
                onRemove = { onRemove(item) }
            )
        }
    }
}

@Composable
private fun AgendaListRow(
    item: Collectible,
    onSelect: () -> Unit,
    onToggleObserved: (Boolean) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable { onSelect() }
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
            AgendaEventIcon(item.eventName)
        }
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.eventName, color = Color.White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(item.observationDate, color = Color(0xFF94A3B8), style = MaterialTheme.typography.labelSmall)
            Text(getCountdownText(item.eventId), color = Color(0xFF818CF8), style = MaterialTheme.typography.labelSmall)
            if (!item.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.notes!!, color = Color(0xFFCBD5E1), style = MaterialTheme.typography.bodySmall)
            }
        }

        val isPast = item.eventId <= System.currentTimeMillis()
        if (isPast) {
            Checkbox(checked = item.observed == 1L, onCheckedChange = onToggleObserved)
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }

        IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Remove",
                tint = Color(0xFFFCA5A5),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
