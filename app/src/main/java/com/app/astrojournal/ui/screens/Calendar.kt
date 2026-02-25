package com.app.astrojournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.astrojournal.R
import com.app.astrojournal.ui.theme.AstrojournalTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

// Colores personalizados para el estilo de la imagen
val DarkSpace = Color(0xFF05051E)
val GridGray = Color.Gray.copy(alpha = 0.3f)
val HighlightBlue = Color(0xFF5C5CFF)
// Localización en español
val localeSpanish = Locale("es", "ES")

@Composable
fun CalendarScreen(modifier: Modifier = Modifier) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = DarkSpace
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            CalendarHeader(
                currentMonth = currentMonth,
                onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            DaysOfWeekHeader()
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
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Anterior", tint = Color.White)
        }
        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, localeSpanish).replaceFirstChar { it.uppercase() }} De ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Siguiente", tint = Color.White)
        }
    }
}

@Composable
fun DaysOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth().border(0.5.dp, GridGray)) {
        val sortedDays = listOf(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
        )

        for (day in sortedDays) {
            Text(
                text = day.getDisplayName(TextStyle.SHORT, localeSpanish).lowercase(),
                modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun CalendarGrid(currentMonth: YearMonth) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek
    val offset = (firstDayOfMonth.value - DayOfWeek.MONDAY.value + 7) % 7
    val today = LocalDate.now()

    Column(modifier = Modifier.fillMaxWidth().border(0.5.dp, GridGray)) {
        var dayOfMonth = 1
        val totalCells = if (offset + daysInMonth > 35) 42 else 35

        for (i in 0 until totalCells / 7) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (j in 0 until 7) {
                    val cellIndex = i * 7 + j
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(0.85f)
                            .border(0.5.dp, GridGray)
                            .clickable(enabled = cellIndex >= offset && dayOfMonth <= daysInMonth) {
                                // Acción opcional
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (cellIndex >= offset && dayOfMonth <= daysInMonth) {
                            val date = currentMonth.atDay(dayOfMonth)
                            val isToday = date == today && currentMonth == YearMonth.now()

                            DayCell(dayOfMonth, date, isToday)
                            dayOfMonth++
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(day: Int, date: LocalDate, isToday: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .then(
                if (isToday) Modifier.border(2.dp, HighlightBlue.copy(alpha = 0.6f))
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        MoonPhaseImage(date = date)

        Text(
            text = day.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Light,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun MoonPhaseImage(date: LocalDate) {
    val phase = (date.dayOfMonth - 1) % 28

    val imageRes = when (phase) {
        0 -> R.drawable.luna_nueva
        in 1..6 -> R.drawable.creciente_iluminante
        7 -> R.drawable.cuarto_creciente
        in 8..13 -> R.drawable.gibosa_creciente
        14 -> R.drawable.luna_llena
        in 15..20 -> R.drawable.gibosa_menguante
        21 -> R.drawable.cuarto_menguante
        else -> R.drawable.creciente_menguante
    }

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize(0.85f)
            .alpha(0.6f),
        contentScale = ContentScale.Fit
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CalendarPreview() {
    AstrojournalTheme {
        CalendarScreen()
    }
}
