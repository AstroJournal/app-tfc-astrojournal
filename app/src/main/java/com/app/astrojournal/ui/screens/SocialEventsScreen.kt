package com.app.astrojournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.astrojournal.R
import com.app.astrojournal.ui.components.AstroBottomNavigation
import com.app.astrojournal.ui.viewmodels.SocialEventsViewModel
import com.astrojournal.shared.data.db.MeetupEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialEventsScreen(
    viewModel: SocialEventsViewModel,
    initialAstroEventName: String? = null,
    currentScreen: String = "social",
    onNavigate: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    // For Dialog
    var showDialog by remember { mutableStateOf(initialAstroEventName != null) }
    var editingEvent by remember { mutableStateOf<MeetupEvent?>(null) }

    // If navigated with initial event, make sure the dialog is open for creation
    LaunchedEffect(initialAstroEventName) {
        if (initialAstroEventName != null) {
            editingEvent = null
            showDialog = true
        }
    }

    Scaffold(
        topBar = { com.app.astrojournal.ui.components.AstroTopBar(onProfileClick = { onNavigate("profile") }) },
        bottomBar = { AstroBottomNavigation(currentScreen = currentScreen, onNavigate = onNavigate) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    editingEvent = null
                    showDialog = true 
                },
                containerColor = Color(0xFF6366F1),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create new event")
            }
        },
        containerColor = Color.Transparent,
        contentColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B1426))
        ) {
            // Fondo estrellado, re-utilizando alpha como en el Calendar
            Image(
                painter = painterResource(id = R.drawable.stary_night_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.6f),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Header
                Text(
                    text = "Astronomical Events",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Meetups and gatherings",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF94A3B8)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Búsqueda
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search event or location...", color = Color(0xFF475569)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF475569)) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedBorderColor = Color(0xFF334155),
                        cursorColor = Color(0xFF6366F1),
                        focusedContainerColor = Color.Black.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pestañas
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color(0xFF6366F1)
                        )
                    },
                    divider = { HorizontalDivider(color = Color(0xFF334155)) }
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("All", fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("My Events", fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de Eventos
                val eventsToShow = if (selectedTabIndex == 0) uiState.allEvents else uiState.myEvents
                val filteredEvents = eventsToShow.filter {
                    it.title.contains(uiState.searchQuery, ignoreCase = true) || 
                    it.location.contains(uiState.searchQuery, ignoreCase = true)
                }

                if (filteredEvents.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No events found",
                            color = Color(0xFF94A3B8),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredEvents) { event ->
                            MeetupEventCard(
                                event = event,
                                onEdit = { 
                                    editingEvent = event
                                    showDialog = true 
                                },
                                onDelete = { viewModel.deleteEvent(event.id) }
                            )
                        }
                    }
                }
            }
        }
        
        if (showDialog) {
            MeetupEventDialog(
                initialEvent = editingEvent,
                initialLinkedEventName = initialAstroEventName,
                upcomingEvents = uiState.upcomingAstroEvents,
                onDismiss = { showDialog = false },
                onSave = { title, desc, loc, date, linkedEventName ->
                    if (editingEvent == null) {
                        viewModel.createEvent(title, desc, loc, date, linkedEventName)
                    } else {
                        viewModel.updateEvent(editingEvent!!.id, title, desc, loc, date, linkedEventName)
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun MeetupEventCard(
    event: MeetupEvent,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.7f)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.dateTime,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFF6366F1),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                if (event.isMine == 1L) {
                    Row {
                        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF94A3B8), modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFCA5A5), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "📍 ${event.location}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFCBD5E1))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8))
            )
            
            if (!event.linkedAstroEventName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xFF6366F1).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🔭 Linked to: ${event.linkedAstroEventName}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA5B4FC))
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetupEventDialog(
    initialEvent: MeetupEvent?,
    initialLinkedEventName: String? = null,
    upcomingEvents: List<com.app.astrojournal.data.model.AstroEvent>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String?) -> Unit
) {
    var title by remember { mutableStateOf(initialEvent?.title ?: "") }
    var description by remember { mutableStateOf(initialEvent?.description ?: "") }
    var location by remember { mutableStateOf(initialEvent?.location ?: "") }
    var dateTime by remember { mutableStateOf(initialEvent?.dateTime ?: "") }
    
    // Dropdown state
    var expanded by remember { mutableStateOf(false) }
    var selectedLinkedEvent by remember { 
        mutableStateOf(initialEvent?.linkedAstroEventName ?: initialLinkedEventName ?: "") 
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E293B),
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = { Text(if (initialEvent == null) "Create Event" else "Edit Event") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", color = Color(0xFF94A3B8)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6366F1),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description", color = Color(0xFF94A3B8)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6366F1),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location", color = Color(0xFF94A3B8)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6366F1),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dateTime,
                    onValueChange = { dateTime = it },
                    label = { Text("Date and time", color = Color(0xFF94A3B8)) },
                    placeholder = { Text("e.g. 2026-08-12 22:00", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6366F1),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Selector de evento astronómico enlazado
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = if (selectedLinkedEvent.isBlank()) "None" else selectedLinkedEvent,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Link to astronomical event", color = Color(0xFF94A3B8)) },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6366F1),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color(0xFF1E293B))
                    ) {
                        DropdownMenuItem(
                            text = { Text("None", color = Color.White) },
                            onClick = {
                                selectedLinkedEvent = ""
                                expanded = false
                            }
                        )
                        upcomingEvents.forEach { astroEvent ->
                            DropdownMenuItem(
                                text = { 
                                    Column {
                                        Text(astroEvent.name, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text(astroEvent.date, color = Color(0xFF94A3B8), style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                onClick = {
                                    selectedLinkedEvent = astroEvent.name
                                    // Autocompletar el título si estaba vacío
                                    if (title.isBlank()) {
                                        title = "Meetup: ${astroEvent.name}"
                                    }
                                    if (dateTime.isBlank()) {
                                        dateTime = astroEvent.date
                                    }
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val finalLinked = selectedLinkedEvent.takeIf { it.isNotBlank() }
                    onSave(title, description, location, dateTime, finalLinked) 
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                enabled = title.isNotBlank() && location.isNotBlank() && dateTime.isNotBlank()
            ) {
                Text("Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        }
    )
}
