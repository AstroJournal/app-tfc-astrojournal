package com.app.astrojournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.astrojournal.utils.getMoonPhaseIcon
import com.app.astrojournal.utils.generateWeeklyMoonForecast


@Composable
fun ComingWeek(moonAge: Double?) {
    if (moonAge == null) return
    val forecast = generateWeeklyMoonForecast(moonAge)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassPanelBg, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "7-DAY FORECAST",
            color = Indigo200,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            forecast.forEach { day ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = day.dayLabel, 
                        color = Indigo200,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp
                    )

                    Image(
                        painter = painterResource(id = day.imageRes),
                        contentDescription = day.phase,
                        modifier = Modifier.size(24.dp)
                    )

                    // Solo mostramos la primera letra o una versión muy corta si es necesario
                    // para evitar que se desorganice el layout
                    val shortPhase = when(day.phase) {
                        "New Moon" -> "New"
                        "Full Moon" -> "Full"
                        "First Quarter" -> "1st"
                        "Last Quarter" -> "3rd"
                        "Waxing Crescent" -> "Wax"
                        "Waning Crescent" -> "Wan"
                        "Waxing Gibbous" -> "Wax"
                        "Waning Gibbous" -> "Wan"
                        else -> ""
                    }
                    
                    Text(
                        text = shortPhase, 
                        color = TextGray400,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}


@Composable
fun DayItem(day: String, phase: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = day, color = Indigo200, fontSize = 10.sp)

        // Icono de la luna según fase
        Image(
            painter = painterResource(id = getMoonPhaseIcon(phase)),
            contentDescription = phase,
            modifier = Modifier.size(24.dp)
        )

        Text(text = label, color = TextGray400, fontSize = 10.sp)
    }
}
