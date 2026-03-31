package com.app.astrojournal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.astrojournal.data.model.ApodUi
import com.app.astrojournal.ui.viewmodels.EventOfTheDayViewModel
import com.app.astrojournal.ui.viewmodels.RemoteUiState

/**
 * Compact card showing today's NASA Astronomy Picture of the Day.
 * Placed in HomeScreen between the 7-day forecast and the Coming Events list.
 * Tapping it navigates to the full EventOfTheDay screen.
 */
@Composable
fun TodayEventCard(
    viewModel: EventOfTheDayViewModel,
    onNavigateToEventOfTheDay: () -> Unit
) {
    val apodState = viewModel.apodState

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassPanelBg)
            .border(1.dp, White10, RoundedCornerShape(16.dp))
            .clickable { onNavigateToEventOfTheDay() }
            .padding(16.dp)
    ) {
        // Section header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Historical Event",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextGray100,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "See more →",
                style = MaterialTheme.typography.labelSmall.copy(color = Indigo300)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (apodState) {
            is RemoteUiState.Loading -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Indigo300
                    )
                    Text(
                        "Loading today's event...",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextGray400)
                    )
                }
            }

            is RemoteUiState.Error -> {
                Text(
                    "Could not load today's event.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextGray400)
                )
            }

            is RemoteUiState.Success -> {
                TodayEventContent(apod = apodState.data)
            }
        }
    }
}

@Composable
private fun TodayEventContent(apod: ApodUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Thumbnail image
        if (apod.imageUrl.isNotBlank()) {
            AsyncImage(
                model = apod.imageUrl,
                contentDescription = apod.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F172A)),
                contentScale = ContentScale.Crop
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = apod.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Indigo200,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = apod.description,
                style = MaterialTheme.typography.bodySmall.copy(color = TextGray400),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
