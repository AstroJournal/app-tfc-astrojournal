package com.app.astrojournal.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.app.astrojournal.ui.components.AstroBottomNavigation
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import java.util.Locale
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import com.app.astrojournal.ui.viewmodels.HomeViewModel
import com.app.astrojournal.ui.viewmodels.UiState
import com.app.astrojournal.R
import com.app.astrojournal.ui.screens.ComingWeek
import androidx.compose.ui.zIndex
import com.app.astrojournal.data.model.AstroEvent


// Colors from Design
val BackgroundDark = Color(0xFF050508)
val BackgroundGradientEnd = Color(0xFF1A1B3A)
val PrimaryIndigo = Color(0xFF6366F1)
val GlassPanelBg = Color(0xFF121629).copy(alpha = 0.65f)
val SurfaceDark = Color(0xFF121423).copy(alpha = 0.75f)
val TextGray100 = Color(0xFFF3F4F6)
val TextGray400 = Color(0xFF9CA3AF)
val Indigo200 = Color(0xFFC7D2FE)
val Indigo300 = Color(0xFFA5B4FC)
val Indigo500 = Color(0xFF6366F1)
val White10 = Color.White.copy(alpha = 0.1f)
private val localeEnglish = Locale.ENGLISH



@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    currentScreen: String = "home",
    onNavigate: (String) -> Unit = {},
    onEventSelected: (AstroEvent) -> Unit = {}
) {
    val moonData = viewModel.moonData.collectAsState()
    val uiState = viewModel.uiState.collectAsState()
    
    // State for forecast selection - initialized to Today by default
    val (selectedForecastDay, setSelectedForecastDay) = androidx.compose.runtime.remember { 
        val initialForecast = com.app.astrojournal.utils.generateWeeklyMoonForecast()
        androidx.compose.runtime.mutableStateOf<com.app.astrojournal.data.model.WeeklyMoonDay?>(initialForecast.firstOrNull()) 
    }

    LaunchedEffect(Unit) {
        viewModel.fetchMoonData()
    }

    Scaffold(
        bottomBar = { AstroBottomNavigation(currentScreen = currentScreen, onNavigate = onNavigate) },
        containerColor = BackgroundDark,
        contentColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BackgroundDark, BackgroundGradientEnd)
                    )
                )
        ) {

            // Imagen de fondo de estrellas
            Image(
                painter = painterResource(id = R.drawable.stary_night_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.6f),
                contentScale = ContentScale.Crop
            )

            // Mostrar contenido basado en el estado de la UI
            when (val state = uiState.value) {
                is UiState.Loading -> {
                    // Mostrar indicador de carga
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(
                                color = Indigo300,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Loading moon data...",
                                style = MaterialTheme.typography.bodyLarge.copy(color = TextGray400)
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    // Mostrar mensaje de error
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "⚠️",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Text(
                                text = "Error",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = TextGray100,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            )
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextGray400),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            androidx.compose.material3.Button(
                                onClick = { viewModel.fetchMoonData() },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = Indigo500
                                )
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is UiState.Success -> {
                    // Observar la lista de eventos astronómicos próximos
                    val events = viewModel.upcomingEvents.collectAsState()

                    // Mostrar contenido normal en un LazyColumn para scroll fluido
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 40.dp + innerPadding.calculateTopPadding(),
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 24.dp + innerPadding.calculateBottomPadding()
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Cabecera con la fase lunar (actual o seleccionada)
                        item { 
                            if (selectedForecastDay != null) {
                                SelectedForecastHeader(selectedForecastDay)
                            } else {
                                MoonHeader(moonData.value)
                            }
                        }
                        
                        // Vista detallada de la próxima semana
                        item { 
                            ComingWeek(
                                selectedDate = selectedForecastDay?.date,
                                onDateSelected = { setSelectedForecastDay(if (it.date == selectedForecastDay?.date) null else it) }
                            ) 
                        }
                        
                        // Lista de eventos astronómicos dinámicos
                        item { CelestialObjectsList(events = events.value, onEventClick = onEventSelected) }
                    }
                }
            }
        }
    }
}


@Composable
fun SelectedForecastHeader(day: com.app.astrojournal.data.model.WeeklyMoonDay) {
    val phaseNameEs = getPhaseNameInSpanish(day.phase)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        // Label de fecha seleccionada (Full day name in English)
        val dayName = day.date.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, localeEnglish)
            .replaceFirstChar { it.uppercase() }
            
        Text(
            text = dayName,
            style = MaterialTheme.typography.labelLarge.copy(
                color = Indigo300,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .size(256.dp)
                .padding(bottom = 16.dp)
        ) {
            Crossfade(targetState = day.imageRes, label = "ForecastMoonCrossfade") { imageRes ->
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = day.phase,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit // Usar Fit como en el calendario
                )
            }
        }

        Text(
            text = phaseNameEs,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = TextGray100
            )
        )

        Text(
            text = "Moon Age: ${"%.1f".format(day.age)} days",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Indigo200,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            ),
            modifier = Modifier.padding(top = 4.dp)
        )

        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .background(Color(0xFF312E81).copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .border(1.dp, Indigo500.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "Illumination: ${day.illumination}%",
                style = MaterialTheme.typography.labelSmall.copy(color = TextGray400)
            )
        }
    }
}

// Helper: nombre de fase en español (Copinado de Calendar para consistencia)
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

@Composable
fun MoonHeader(moonData: com.app.astrojournal.data.model.Astro?) {
    if (moonData == null) return

    val age = moonData.moon_age ?: 0.0
    val illumination = moonData.moon_illumination ?: calculateIllumination(age).toString()
    val phaseName = moonData.moon_phase ?: "Full Moon"
    val moonImageRes = getMoonPhaseImage(phaseName)


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(256.dp)
                .padding(bottom = 16.dp)
        ) {
            Crossfade(targetState = moonImageRes, label = "MoonPhaseCrossfade") { imageRes ->
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = phaseName,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Text(
            text = phaseName,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = TextGray100
            )
        )

        Text(
            text = "Moon Age: ${"%.1f".format(age)} days",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Indigo200,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            ),
            modifier = Modifier.padding(top = 4.dp)
        )

        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .background(Color(0xFF312E81).copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .border(1.dp, Indigo500.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "Illumination: $illumination%",
                style = MaterialTheme.typography.labelSmall.copy(color = TextGray400)
            )
        }
    }
}



fun getPhaseFromMoonAge(age: Double): String = when {
    age < 1.8 -> "New Moon"
    age < 5.5 -> "Waxing Crescent"
    age < 9.2 -> "First Quarter"
    age < 12.9 -> "Waxing Gibbous"
    age < 16.6 -> "Full Moon"
    age < 20.3 -> "Waning Gibbous"
    age < 24.1 -> "Last Quarter"
    else -> "Waning Crescent"
}

// -----------------------------
// Mapea nombre de fase a imagen
// -----------------------------
fun getMoonPhaseImage(phaseName: String): Int {
    return when (phaseName.lowercase().replace(" ", "_")) {
        "new_moon" -> R.drawable.new_moon
        "waxing_crescent" -> R.drawable.waxing_crescent
        "first_quarter" -> R.drawable.first_quarter
        "waxing_gibbous" -> R.drawable.waxing_gibbous
        "full_moon" -> R.drawable.full_moon
        "waning_gibbous" -> R.drawable.waning_gibbous
        "last_quarter" -> R.drawable.third_quarter
        "waning_crescent" -> R.drawable.waning_crescent
        else -> R.drawable.full_moon
    }
}



// -----------------------------
// Iluminación aproximada de la luna
// -----------------------------
fun calculateIllumination(age: Double): Int {
    // Simple: 0-14 días creciente, 14-29 decreciente
    return if (age <= 14.0) ((age / 14.0) * 100).toInt()
    else (100 - ((age - 14) / 15.53 * 100)).toInt()
}



