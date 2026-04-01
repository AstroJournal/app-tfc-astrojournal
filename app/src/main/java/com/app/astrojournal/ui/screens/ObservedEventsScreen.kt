package com.app.astrojournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.astrojournal.R
import com.app.astrojournal.ui.components.AstroBottomNavigation
import com.app.astrojournal.ui.viewmodels.ObservedEventsViewModel
import com.astrojournal.shared.data.db.Collectible

@Composable
fun ObservedEventsScreen(
    viewModel: ObservedEventsViewModel,
    currentScreen: String = "observed",
    onNavigate: (String) -> Unit = {}
) {
    val observed = viewModel.observed.collectAsState()
    var editingNoteFor by remember { mutableStateOf<Collectible?>(null) }
    var removingFor by remember { mutableStateOf<Collectible?>(null) }

    // Reload every time this screen is shown so newly observed events appear
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    if (removingFor != null) {
        AlertDialog(
            onDismissRequest = { removingFor = null },
            title = {
                Text(
                    text = "Remove from observed",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                )
            },
            text = {
                Text(
                    "Are you sure you want to remove '${removingFor?.eventName}' from the observed events list? It will return to the pending past events list.",
                    color = Color.White
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.unmarkAsObserved(removingFor!!.id)
                    removingFor = null
                }) {
                    Text("Remove", color = Color(0xFFFCA5A5))
                }
            },
            dismissButton = {
                TextButton(onClick = { removingFor = null }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }

    if (editingNoteFor != null) {
        var noteText by remember { mutableStateOf(editingNoteFor?.notes ?: "") }
        AlertDialog(
            onDismissRequest = { editingNoteFor = null },
            title = {
                Text(
                    text = "Edit Note",
                    style = MaterialTheme.typography.titleMedium.copy(color = TextGray100)
                )
            },
            text = {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Note") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Indigo500,
                        unfocusedBorderColor = White10,
                        focusedLabelColor = Indigo300,
                        unfocusedLabelColor = TextGray400,
                        focusedTextColor = TextGray100,
                        unfocusedTextColor = TextGray100
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateNote(editingNoteFor!!.id, noteText)
                    editingNoteFor = null
                }) {
                    Text("Save", color = Indigo300)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingNoteFor = null }) {
                    Text("Cancel", color = TextGray400)
                }
            },
            containerColor = BackgroundDark,
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    Scaffold(
        topBar = { com.app.astrojournal.ui.components.AstroTopBar(onProfileClick = { onNavigate("profile") }) },
        bottomBar = { AstroBottomNavigation(currentScreen = currentScreen, onNavigate = onNavigate) },
        containerColor = BackgroundDark,
        contentColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BackgroundDark, BackgroundGradientEnd)))
        ) {
            Image(
                painter = painterResource(id = R.drawable.stary_night_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().alpha(0.6f),
                contentScale = ContentScale.Crop
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 40.dp + innerPadding.calculateTopPadding(),
                    start = 16.dp, end = 16.dp,
                    bottom = 24.dp + innerPadding.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Observed Events",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = TextGray100,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Events you have marked as observed",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextGray400)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (observed.value.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Outlined.Visibility,
                                    contentDescription = null,
                                    tint = Indigo300,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No observed events yet",
                                    style = MaterialTheme.typography.bodyLarge.copy(color = TextGray400)
                                )
                                Text(
                                    "Mark events as observed from the event detail screen",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextGray400),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(observed.value) { collectible ->
                        ObservedEventItem(
                            collectible = collectible,
                            onEditClick = { editingNoteFor = collectible },
                            onRemoveClick = { removingFor = collectible }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ObservedEventItem(
    collectible: Collectible,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GlassPanelBg)
            .border(1.dp, White10, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Indigo500.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Visibility,
                contentDescription = null,
                tint = Indigo300,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = collectible.eventName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextGray100,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = collectible.observationDate,
                style = MaterialTheme.typography.bodySmall.copy(color = TextGray400)
            )
            collectible.notes?.let { note ->
                if (note.isNotBlank()) {
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodySmall.copy(color = Indigo200),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit Note",
                tint = Color(0xFF93C5FD)
            )
        }

        IconButton(onClick = onRemoveClick) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Filled.Delete,
                contentDescription = "Remove Observed",
                tint = Color(0xFFFCA5A5)
            )
        }
    }
}
