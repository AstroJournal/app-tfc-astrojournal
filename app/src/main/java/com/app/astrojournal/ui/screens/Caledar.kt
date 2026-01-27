package com.app.astrojournal.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.Lens
import androidx.compose.material.icons.filled.Tonality
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.astrojournal.ui.theme.AstrojournalTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import coil.compose.AsyncImage

@Composable
fun CalendarScreen(modifier: Modifier = Modifier) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CalendarHeader(
                currentMonth = currentMonth,
                onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
            )
            Spacer(modifier = Modifier.height(24.dp))
            DaysOfWeekHeader()
            Spacer(modifier = Modifier.height(12.dp))
            CalendarGrid(currentMonth)
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Anterior",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNextMonth) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Siguiente",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DaysOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        val daysOfWeek = DayOfWeek.entries
        val firstDay = DayOfWeek.MONDAY
        val sortedDays = daysOfWeek.sortedBy {
            val day = it.value - firstDay.value
            if (day < 0) day + 7 else day
        }

        for (day in sortedDays) {
            Text(
                text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun CalendarGrid(currentMonth: YearMonth) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek
    val offset = (firstDayOfMonth.value - DayOfWeek.MONDAY.value + 7) % 7
    val context = LocalContext.current
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
                        val isToday = date == today && currentMonth == YearMonth.now()

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isToday) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                                )
                                .clickable {
                                    Toast.makeText(
                                        context,
                                        "Día seleccionado: $date",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayOfMonth.toString(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                )
                                MoonPhaseIcon(date = date)
                            }
                        }
                        dayOfMonth++
                    } else {
                        Spacer(modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun MoonPhaseIcon(date: LocalDate) {
    val phase = (date.dayOfMonth - 1) % 28

    // URLs de imágenes reales de la luna
    val moonUrl = when {
        phase < 3 || phase > 25 -> null // Luna nueva
        phase in 3..10 -> "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b3/First_quarter_moon_near_perigee.jpg/120px-First_quarter_moon_near_perigee.jpg"
        phase in 11..17 -> "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e1/FullMoon2010.jpg/120px-FullMoon2010.jpg"
        else -> "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Last_quarter_moon_near_apogee.jpg/120px-Last_quarter_moon_near_apogee.jpg"
    }

    if (moonUrl != null) {
        AsyncImage(
            model = moonUrl,
            contentDescription = "Luna",
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CalendarPreview() {
    AstrojournalTheme {
        CalendarScreen()
    }
}
