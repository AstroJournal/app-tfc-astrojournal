package com.app.astrojournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.astrojournal.data.model.WeeklyMoonDay
import com.app.astrojournal.utils.generateWeeklyMoonForecast
import java.time.LocalDate

@Composable
fun ComingWeek(
    selectedDate: LocalDate?,
    onDateSelected: (WeeklyMoonDay) -> Unit,
    onNavigateToCalendar: (() -> Unit)? = null
) {
    val forecast = generateWeeklyMoonForecast()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassPanelBg, RoundedCornerShape(16.dp))
            .border(1.dp, White10, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "7-DAY FORECAST",
                color = Indigo200,
                style = MaterialTheme.typography.labelSmall
            )
            if (onNavigateToCalendar != null) {
                Text(
                    text = "Full calendar →",
                    color = Indigo300,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.clickable { onNavigateToCalendar() }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            forecast.forEach { day ->
                val isSelected = day.date == selectedDate
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Indigo500.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { onDateSelected(day) }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = day.dayLabel, 
                        color = if (isSelected) Color.White else Indigo200,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                    )

                    Image(
                        painter = painterResource(id = day.imageRes),
                        contentDescription = day.phase,
                        modifier = Modifier.size(24.dp)
                    )

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
                        color = if (isSelected) Indigo200 else TextGray400,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}
