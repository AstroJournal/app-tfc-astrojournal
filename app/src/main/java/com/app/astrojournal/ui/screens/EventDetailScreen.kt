package com.app.astrojournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.astrojournal.R
import com.app.astrojournal.eventdetail.EventDetailViewModel
import com.app.astrojournal.model.Event
import com.app.astrojournal.ui.components.AstroBottomNavigation

@Composable
fun EventDetailScreen(
    event: Event,
    viewModel: EventDetailViewModel
) {
    val collectibles = viewModel.collectibles
    var newNote by remember { mutableStateOf("") }

    val planetPainter = painterResource(id = event.planetImageRes)

    Scaffold(
        bottomBar = { AstroBottomNavigation() },
        containerColor = Color.Transparent,
        contentColor = Color.White
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            //  Fondo estrellado
            Image(
                painter = painterResource(id = R.drawable.stary_night_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            //Imagen del evento
            Image(
                painter = planetPainter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.7f),
                contentScale = ContentScale.Crop
            )

            // Panel de contenido
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF121629).copy(alpha = 0.65f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp))
                    .padding(24.dp)
            ) {

                // Título y fecha
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Text(
                    text = event.dateTime,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF9CA3AF)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Checkbox de observado
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = if (viewModel.isEventObserved)
                            "¡Has observado este evento!"
                        else
                            "Marcar como observado",
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Checkbox(
                        checked = viewModel.isEventObserved,
                        onCheckedChange = { checked ->
                            viewModel.markObserved(checked)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF4F46E5),
                            uncheckedColor = Color(0xFF9CA3AF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo para añadir notas
                OutlinedTextField(
                    value = newNote,
                    onValueChange = { newNote = it },
                    label = { Text("Add note", color = Color(0xFF9CA3AF)) },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4F46E5),
                        unfocusedBorderColor = Color(0xFF9CA3AF),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color(0xFF9CA3AF),
                        cursorColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Botón para guardar nota
                Button(
                    onClick = {
                        if (newNote.isNotBlank()) {
                            viewModel.addNote(event.id, newNote)
                            newNote = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F46E5)
                    )
                ) {
                    Text("Save note", color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Lista de notas
                val eventNotes = collectibles.filter { it.eventId == event.id }

                if (eventNotes.isNotEmpty()) {

                    Text("Notes:", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))

                    eventNotes.forEach { item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "• ${item.notes}",
                                color = Color(0xFF9CA3AF),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { viewModel.deleteNote(item.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4F46E5)
                                )
                            ) {
                                Text("Delete", color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
