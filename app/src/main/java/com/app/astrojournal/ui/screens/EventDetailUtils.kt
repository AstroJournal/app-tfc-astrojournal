package com.app.astrojournal.ui.screens

import androidx.compose.runtime.Composable
import com.app.astrojournal.R
import com.app.astrojournal.data.model.EventType
import com.app.astrojournal.ui.components.CometIcon
import com.app.astrojournal.ui.components.PlanetIcon
import com.app.astrojournal.ui.components.StarIconShape
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun AgendaEventIcon(eventName: String) {
    val normalized = eventName.lowercase(Locale.ROOT)
    when {
        "opposition" in normalized ||
            "mars" in normalized ||
            "jupiter" in normalized ||
            "saturn" in normalized ||
            "venus" in normalized -> PlanetIcon()

        "eclipse" in normalized || "comet" in normalized -> CometIcon()

        "moon" in normalized || "luna" in normalized -> StarIconShape(color = Indigo300)

        else -> StarIconShape(color = Indigo200)
    }
}

fun inferEventType(eventName: String): EventType {
    val normalized = eventName.lowercase(Locale.ROOT)
    return when {
        "opposition" in normalized || "mars" in normalized || "jupiter" in normalized || "saturn" in normalized || "venus" in normalized -> EventType.PLANET
        "eclipse" in normalized -> EventType.CONJUNCTION
        "moon" in normalized || "luna" in normalized -> EventType.MOON_PHASE
        else -> EventType.OTHER
    }
}

fun formatLocalDateTime(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.getDefault())
    return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).format(formatter)
}

fun getCountdownText(timestamp: Long): String {
    val now = LocalDate.now()
    val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
    val days = ChronoUnit.DAYS.between(now, date)
    return when {
        days > 1 -> "Faltan $days dias"
        days == 1L -> "Falta 1 dia"
        days == 0L -> "Es hoy"
        days == -1L -> "Fue ayer"
        else -> "Fue hace ${-days} dias"
    }
}

fun getEventBackgroundImage(eventName: String): Int {
    val normalized = eventName.lowercase(Locale.ROOT)
    return when {
        "eclipse" in normalized -> R.drawable.eclipse
        "moon" in normalized || "luna" in normalized || "quarter" in normalized || "gibbous" in normalized || "crescent" in normalized -> R.drawable.full_moon
        "mars" in normalized || "jupiter" in normalized || "saturn" in normalized || "venus" in normalized || "opposition" in normalized -> R.drawable.moon
        else -> R.drawable.moon
    }
}
