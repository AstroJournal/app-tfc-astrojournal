package com.app.astrojournal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.astrojournal.data.model.AstroEvent
import com.app.astrojournal.data.model.EventType
import com.app.astrojournal.ui.components.CometIcon
import com.app.astrojournal.ui.components.PlanetIcon
import com.app.astrojournal.ui.components.StarIconShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

/**
 * Muestra una lista de objetos y eventos celestiales próximos.
 * @param events Lista de eventos astronómicos calculados.
 * @param onEventClick Callback invocado cuando el usuario pulsa un evento.
 */
@Composable
fun CelestialObjectsList(
    events: List<AstroEvent>,
    onEventClick: (AstroEvent) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassPanelBg, RoundedCornerShape(16.dp))
            .padding(vertical = 8.dp)
    ) {
        // Título de la sección
        Text(
            text = "Coming Events",
            style = MaterialTheme.typography.titleMedium.copy(
                color = TextGray100,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Si no hay eventos, mostrar un mensaje informativo
        if (events.isEmpty()) {
            Text(
                text = "No upcoming events found.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextGray400),
                modifier = Modifier.padding(16.dp)
            )
        }

        // Iterar sobre los eventos astronómicos
        events.forEach { event ->
            CelestialItem(
                name = event.name,
                description = event.description,
                date = event.date,
                onClick = { onEventClick(event) },
                icon = {
                    // Seleccionar el icono adecuado según el tipo de evento
                    when (event.type) {
                        EventType.MOON_PHASE -> StarIconShape(color = Indigo300)
                        EventType.PLANET -> PlanetIcon()
                        EventType.CONJUNCTION -> CometIcon() // Usar Comet como placeholder para conjunciones
                        else -> StarIconShape(color = Indigo200)
                    }
                }
            )
        }
    }
}

/**
 * Representa un elemento individual en la lista de objetos celestiales.
 */
@Composable
fun CelestialItem(
    name: String,
    description: String,
    date: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Contenedor del icono con fondo circular sutil
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Indigo500.copy(alpha = 0.1f), CircleShape),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            icon()
        }

        // Información del evento
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextGray100,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(color = TextGray400)
            )
        }

        // Fecha del evento resaltada
        Text(
            text = date,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Indigo300,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

