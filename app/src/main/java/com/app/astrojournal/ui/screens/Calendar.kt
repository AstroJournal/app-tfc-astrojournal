package com.app.astrojournal.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import java.util.Date
import java.util.Locale

// Reutilizamos la localización en inglés
private val localeEnglish = Locale.ENGLISH

@Composable
fun CalendarScreen(
    currentScreen: String = "calendar",
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Scaffold(
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
            // Fondo estrellado
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
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Cabecera del mes con flechas
                CalendarHeader(
                    currentMonth = currentMonth,
                    onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                    onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Panel de cristal con el calendario
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
                        onDateSelected = { selectedDate = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detalle de la fase lunar del día seleccionado
                SelectedDayMoonDetail(selectedDate)
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
                            DayCell(currentDay, date, isToday, isSelected)
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
fun DayCell(day: Int, date: LocalDate, isToday: Boolean, isSelected: Boolean) {
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
        // Imagen de fase lunar de fondo
        MoonPhaseImage(date = date)

        // Número del día
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else if (isToday) Indigo200 else TextGray100,
                fontSize = 13.sp
            ),
            modifier = Modifier.align(Alignment.Center)
        )
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

    val imageRes = getMoonPhaseImage(phaseInfo.phaseName)

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
// Detalle del día seleccionado (como en HomeScreen)
// -----------------------------------------------
@Composable
fun SelectedDayMoonDetail(selectedDate: LocalDate) {
    val javaDate = Date.from(
        selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
    )
    val phaseInfo = remember(selectedDate) {
        MoonCalculator.getMoonPhaseInfo(javaDate)
    }

    val moonImageRes = getMoonPhaseImage(phaseInfo.phaseName)
    val phaseNameEs = getPhaseNameInSpanish(phaseInfo.phaseName)

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
        // Fecha
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

        // Nombre de la fase
        Text(
            text = phaseNameEs,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextGray100
            )
        )

        // Edad de la luna
        Text(
            text = "Edad lunar: ${"%.1f".format(phaseInfo.moonAge)} días",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Indigo200,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(top = 4.dp)
        )

        // Badge de iluminación
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .background(Color(0xFF312E81).copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .border(1.dp, Indigo500.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "Iluminación: ${phaseInfo.illumination}%",
                style = MaterialTheme.typography.labelSmall.copy(color = TextGray400)
            )
        }
    }
}

// Helper: nombre de fase en español
private fun getPhaseNameInSpanish(phaseName: String): String {
    return when (phaseName) {
        "New Moon" -> "Luna Nueva"
        "Waxing Crescent" -> "Creciente Iluminante"
        "First Quarter" -> "Cuarto Creciente"
        "Waxing Gibbous" -> "Gibosa Creciente"
        "Full Moon" -> "Luna Llena"
        "Waning Gibbous" -> "Gibosa Menguante"
        "Last Quarter" -> "Cuarto Menguante"
        "Waning Crescent" -> "Creciente Menguante"
        else -> phaseName
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CalendarPreview() {
    AstrojournalTheme {
        CalendarScreen()
    }
}
