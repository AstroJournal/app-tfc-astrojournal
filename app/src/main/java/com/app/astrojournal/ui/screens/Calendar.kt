package com.app.astrojournal.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.astrojournal.R
import com.app.astrojournal.ui.components.AstroBottomNavigation
import com.app.astrojournal.ui.theme.AstrojournalTheme
import com.app.astrojournal.utils.MoonCalculator
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import com.app.astrojournal.ui.viewmodels.CalendarViewModel
import com.astrojournal.shared.data.db.Collectible
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

// Reutilizamos la localización en inglés
private val localeEnglish = Locale.ENGLISH

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel? = null,
    currentScreen: String = "calendar",
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val collectibles = viewModel?.collectibles?.collectAsState()
    val allCollectibles = collectibles?.value ?: emptyList()

    // Reload data every time this screen is shown so calendar dots stay fresh
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel?.load()
    }

    // Set of dates that have agended events (formatted as ISO date string)
    val agendedDates = remember(allCollectibles) {
        allCollectibles
            .filter { it.agended == 1L }
            .map { 
                java.time.Instant.ofEpochMilli(it.eventId)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                    .toString()
            }
            .toSet()
    }

    Scaffold(
        topBar = { com.app.astrojournal.ui.components.AstroTopBar(onProfileClick = { onNavigate("profile") }) },
        bottomBar = { AstroBottomNavigation(currentScreen = currentScreen, onNavigate = onNavigate) },
        containerColor = Color.Transparent,
        contentColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BackgroundDark, BackgroundGradientEnd)
                    )
                )
        ) {
            // Starry background
            Image(
                painter = painterResource(id = R.drawable.stary_night_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.6f),
                contentScale = ContentScale.Crop
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 16.dp + innerPadding.calculateTopPadding(),
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 24.dp + innerPadding.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Month header with arrows
                item {
                    CalendarHeader(
                        currentMonth = currentMonth,
                        onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                        onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
                    )
                }

                // Glass panel with the calendar grid
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(GlassPanelBg)
                            .border(1.dp, White10, RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        DaysOfWeekHeader()
                        Spacer(modifier = Modifier.height(4.dp))
                        CalendarGrid(
                            currentMonth = currentMonth,
                            selectedDate = selectedDate,
                            agendedDates = agendedDates,
                            onDateSelected = { selectedDate = it }
                        )
                    }
                }

                // Moon + agenda detail for selected day
                item {
                    SelectedDayMoonDetail(
                        selectedDate = selectedDate,
                        collectibles = allCollectibles,
                        onUnagenda = { collectibleId -> viewModel?.unagendaEvent(collectibleId) }
                    )
                }
            }
        }
    }
}


@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassPanelBg)
            .border(1.dp, White10, RoundedCornerShape(16.dp))
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Anterior",
                tint = Indigo300
            )
        }
        Text(
            text = "${
                currentMonth.month.getDisplayName(TextStyle.FULL, localeEnglish)
                    .replaceFirstChar { it.uppercase() }
            } ${currentMonth.year}",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextGray100
            )
        )
        IconButton(onClick = onNextMonth) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Siguiente",
                tint = Indigo300
            )
        }
    }
}

@Composable
fun DaysOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        val sortedDays = listOf(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
        )

        for (day in sortedDays) {
            Text(
                text = day.getDisplayName(TextStyle.SHORT, localeEnglish)
                    .replaceFirstChar { it.uppercase() },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 6.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Indigo200,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    agendedDates: Set<String> = emptySet(),
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek
    val offset = (firstDayOfMonth.value - DayOfWeek.MONDAY.value + 7) % 7
    val today = LocalDate.now()

    Column(modifier = Modifier.fillMaxWidth()) {
        var dayOfMonth = 1
        val totalCells = if (offset + daysInMonth > 35) 42 else 35

        for (i in 0 until totalCells / 7) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (j in 0 until 7) {
                    val cellIndex = i * 7 + j
                    if (cellIndex >= offset && dayOfMonth <= daysInMonth) {
                        val date = currentMonth.atDay(dayOfMonth)
                        val isToday = date == today
                        val isSelected = date == selectedDate
                        val currentDay = dayOfMonth

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(0.85f)
                                .padding(1.dp)
                                .clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            val hasAgendedEvent = agendedDates.contains(date.toString())
                            DayCell(currentDay, date, isToday, isSelected, hasAgendedEvent)
                        }
                        dayOfMonth++
                    } else {
                        // Celda vacía
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(0.85f)
                                .padding(1.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(day: Int, date: LocalDate, isToday: Boolean, isSelected: Boolean, hasAgendedEvent: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .then(
                when {
                    isSelected -> Modifier
                        .background(Indigo500.copy(alpha = 0.35f))
                        .border(1.5.dp, Indigo500, RoundedCornerShape(8.dp))
                    isToday -> Modifier
                        .background(Indigo500.copy(alpha = 0.15f))
                        .border(1.dp, Indigo500.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    else -> Modifier
                        .background(Color.White.copy(alpha = 0.03f))
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Moon phase background image
        MoonPhaseImage(date = date)

        // Day number
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else if (isToday) Indigo200 else TextGray100,
                fontSize = 13.sp
            ),
            modifier = Modifier.align(Alignment.Center)
        )

        // White agenda dot at the bottom of the cell
        if (hasAgendedEvent) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color.White, CircleShape)
            )
        }
    }
}

@Composable
fun MoonPhaseImage(date: LocalDate) {
    val javaDate = Date.from(
        date.atStartOfDay(ZoneId.systemDefault()).toInstant()
    )
    val phaseInfo = remember(date) {
        MoonCalculator.getMoonPhaseInfo(javaDate)
    }

    val imageRes = com.app.astrojournal.utils.MoonUiUtils.getMoonPhaseImage(phaseInfo.phaseName)

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize(0.7f)
            .alpha(0.4f)
            .clip(CircleShape),
        contentScale = ContentScale.Fit
    )
}

// -----------------------------------------------
// Selected day detail: moon phase + agenda events
// -----------------------------------------------
@Composable
fun SelectedDayMoonDetail(
    selectedDate: LocalDate,
    collectibles: List<com.astrojournal.shared.data.db.Collectible> = emptyList(),
    onUnagenda: (Long) -> Unit = {}
) {
    val javaDate = java.util.Date.from(
        selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
    )
    val phaseInfo = remember(selectedDate) {
        MoonCalculator.getMoonPhaseInfo(javaDate)
    }

    val moonImageRes = com.app.astrojournal.utils.MoonUiUtils.getMoonPhaseImage(phaseInfo.phaseName)
    val phaseName = com.app.astrojournal.utils.MoonUiUtils.getPhaseNameInSpanish(phaseInfo.phaseName)

    // Events for selected date using timestamp (eventId) to match exact Calendar LocalDate
    val selectedDateStr = selectedDate.toString()
    val dayCollectibles = collectibles.filter { 
        java.time.Instant.ofEpochMilli(it.eventId)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
            .toString() == selectedDateStr 
    }
    val agendedEvents = dayCollectibles.filter { it.agended == 1L }
    val otherEvents = dayCollectibles.filter { it.agended != 1L }

    // Formato de la fecha seleccionada (Full day name in English)
    val dateLabel = "${selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, localeEnglish)
        .replaceFirstChar { it.uppercase() }}, ${selectedDate.dayOfMonth} ${
        selectedDate.month.getDisplayName(TextStyle.FULL, localeEnglish)
            .replaceFirstChar { it.uppercase() }
    }"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassPanelBg)
            .border(1.dp, White10, RoundedCornerShape(16.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Date header
        Text(
            text = dateLabel,
            style = MaterialTheme.typography.labelLarge.copy(
                color = Indigo200,
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Imagen grande de la fase lunar
        Box(
            modifier = Modifier
                .size(160.dp)
                .padding(bottom = 12.dp)
        ) {
            Crossfade(targetState = moonImageRes, label = "CalendarMoonCrossfade") { imageRes ->
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Phase name (English)
        Text(
            text = phaseName,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextGray100
            )
        )

        // Lunar age
        Text(
            text = "Lunar age: ${"%.1f".format(phaseInfo.moonAge)} days",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Indigo200,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(top = 4.dp)
        )

        // Illumination badge
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .background(Color(0xFF312E81).copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .border(1.dp, Indigo500.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "Illumination: ${phaseInfo.illumination}%",
                style = MaterialTheme.typography.labelSmall.copy(color = TextGray400)
            )
        }

        // --- Events panel ---
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = White10)
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Scheduled events",
            style = MaterialTheme.typography.labelMedium.copy(
                color = Indigo300,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))

        if (agendedEvents.isNotEmpty()) {
            agendedEvents.forEach { item ->
                AgendaEventRow(item, isAgended = true, onUnagenda = onUnagenda)
            }
        } else {
            Text(
                text = "No scheduled events for this day.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextGray400,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        if (otherEvents.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "\uD83D\uDD2D Other records",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = TextGray400,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            otherEvents.forEach { item ->
                AgendaEventRow(item, isAgended = false)
            }
        }
    }
}

@Composable
private fun AgendaEventRow(
    item: com.astrojournal.shared.data.db.Collectible,
    isAgended: Boolean,
    onUnagenda: (Long) -> Unit = {}
) {
    var showConfirm by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Remove from schedule?") },
            text = { Text("Are you sure you want to remove \"${item.eventName}\" from your agenda? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    onUnagenda(item.id)
                }) {
                    Text("Remove", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    if (isAgended) Indigo300 else TextGray400,
                    CircleShape
                )
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.eventName,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextGray100,
                    fontWeight = FontWeight.Medium
                )
            )
            item.notes?.let { note ->
                if (note.isNotBlank()) {
                    Text(
                        text = note,
                        style = MaterialTheme.typography.labelSmall.copy(color = TextGray400)
                    )
                }
            }
        }
        // Delete from agenda button (only shown for agended events)
        if (isAgended) {
            IconButton(
                onClick = { showConfirm = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remove from agenda",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// getPhaseNameInSpanish centralized in MoonUiUtils


//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun CalendarPreview() {
//    AstrojournalTheme {
//        CalendarScreen()
//    }
//}
