package com.app.astrojournal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.astrojournal.ui.screens.Indigo300

@Composable
fun StarIconShape(color: Color = Indigo300) {
    Icon(
        imageVector = Icons.Rounded.Star,
        contentDescription = null,
        tint = color
    )
}

@Composable
fun PlanetIcon() {
    Box(
        modifier = Modifier
            .size(24.dp)
            .border(1.5.dp, Indigo300, CircleShape),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(Indigo300.copy(alpha = 0.3f), CircleShape)
        )
    }
}

@Composable
fun CometIcon() {
    Icon(
        imageVector = Icons.Rounded.Star,
        contentDescription = null,
        tint = Indigo300
    )
}
