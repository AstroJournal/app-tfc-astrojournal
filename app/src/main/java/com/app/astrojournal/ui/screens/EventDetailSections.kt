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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.app.astrojournal.ui.viewmodels.AgendaFilter
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
    onPrimaryAction: () -> Unit,
    onDeleteNote: () -> Unit,
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
            Text("Notas personales", style = MaterialTheme.typography.labelMedium, color = Color(0xFF6366F1))
            if (currentNote.isNotBlank()) {
                TextButton(onClick = onDeleteNote) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar nota",
                        tint = Color(0xFFFCA5A5),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Borrar nota", color = Color(0xFFFCA5A5))
                }
            }
        }

        OutlinedTextField(
            value = noteInput,
            onValueChange = onNoteChange,
            modifier = Modifier.fillMaxWidth().testTag("event_note_input"),
            placeholder = { Text("Anade una nota opcional...", color = Color(0xFF475569)) },
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
                Spacer(modifier = Modifier.width(4.dp))
                Text("Guardar cambios", color = Color(0xFF6366F1))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val actionLabel = if (isFuture) {
            if (isAgended) "Guardar cambios" else "Anadir a mi agenda"
        } else {
            if (isObserved) "Desmarcar observado" else "Marcar como observado"
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
    }
}

@Composable
fun AgendaSection(
    selectedFilter: AgendaFilter,
    filteredEvents: List<Collectible>,
    currentEventId: Long,
    onFilterSelected: (AgendaFilter) -> Unit,
    onSelectEvent: (Collectible) -> Unit,
    onToggleObserved: (Collectible, Boolean) -> Unit,
    onEdit: (Collectible) -> Unit,
    onDelete: (Collectible) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GlassPanelBg)
            .border(1.dp, White10, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Text("Mi Agenda", style = MaterialTheme.typography.titleMedium, color = Color(0xFF818CF8), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        AgendaFilterRow(selectedFilter = selectedFilter, onFilterSelected = onFilterSelected)
        Spacer(modifier = Modifier.height(10.dp))

        filteredEvents.forEach { agendaItem ->
            AgendaItemRow(
                item = agendaItem,
                currentEventId = currentEventId,
                onSelectEvent = onSelectEvent,
                onToggleObserved = onToggleObserved,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }
    }
}

@Composable
private fun AgendaFilterRow(selectedFilter: AgendaFilter, onFilterSelected: (AgendaFilter) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val chipColors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF312E81).copy(alpha = 0.4f),
            selectedLabelColor = Color(0xFFA5B4FC),
            containerColor = Color.White.copy(alpha = 0.05f),
            labelColor = Color(0xFFCBD5E1)
        )

        FilterChip(selected = selectedFilter == AgendaFilter.ALL, onClick = { onFilterSelected(AgendaFilter.ALL) }, colors = chipColors, label = { Text("Todos") })
        FilterChip(selected = selectedFilter == AgendaFilter.PENDING, onClick = { onFilterSelected(AgendaFilter.PENDING) }, colors = chipColors, label = { Text("Pendientes") })
        FilterChip(selected = selectedFilter == AgendaFilter.OBSERVED, onClick = { onFilterSelected(AgendaFilter.OBSERVED) }, colors = chipColors, label = { Text("Observados") })
    }
}

@Composable
private fun AgendaItemRow(
    item: Collectible,
    currentEventId: Long,
    onSelectEvent: (Collectible) -> Unit,
    onToggleObserved: (Collectible, Boolean) -> Unit,
    onEdit: (Collectible) -> Unit,
    onDelete: (Collectible) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable { onSelectEvent(item) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF6366F1).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            AgendaEventIcon(item.eventName)
        }
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.eventName, color = Color.White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(
                text = "${formatLocalDateTime(item.eventId)} · ${getCountdownText(item.eventId)}",
                color = Color(0xFF94A3B8),
                style = MaterialTheme.typography.labelSmall
            )
            if (!item.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.notes!!, color = Color(0xFFCBD5E1), style = MaterialTheme.typography.bodySmall)
            }
        }

        Checkbox(
            checked = item.observed == 1L,
            onCheckedChange = { checked -> onToggleObserved(item, checked) }
        )

        TextButton(onClick = { onEdit(item) }) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Editar nota",
                tint = Color(0xFF93C5FD),
                modifier = Modifier.size(18.dp)
            )
        }

        TextButton(onClick = { onDelete(item) }) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Eliminar",
                tint = Color(0xFFFCA5A5),
                modifier = Modifier.size(18.dp)
            )
        }

        androidx.compose.material3.Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color(0xFF475569),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun DeleteNoteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar nota") },
        text = { Text("Seguro que quieres borrar la nota de este evento?") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Borrar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun DeleteAgendaItemDialog(item: Collectible, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar evento") },
        text = { Text("Seguro que quieres quitar '${item.eventName}' de tu agenda?") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Eliminar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
