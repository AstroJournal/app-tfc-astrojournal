package com.app.astrojournal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Custom Bottom Navigation Bar.
 * - Simulates a glass panel floating at the bottom.
 * - Contains a centered "Floating Action Button" style Moon button.
 * - Uses a gradient fade at the very bottom to blend with the background.
 */
@Composable
fun AstroBottomNavigation(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp) // Height including the floating effect
    ) {
        // Gradient overlay for bottom fade
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f),
                            Color.Black
                        )
                    )
                )
        )

        // Glass panel container
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 24.dp)
                .height(64.dp)
                .fillMaxWidth(0.85f) // Adjust width to match design ~max-w-md
                .clip(CircleShape)
                .background(Color(0xFF121629).copy(alpha = 0.65f))
                .border(1.dp, Color(0xFFFFFFFF).copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavButton(icon = Icons.Outlined.StarOutline, contentDescription = "Stars")
                NavButton(icon = Icons.Outlined.CalendarToday, contentDescription = "Calendar")
                
                // Center Fab
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4F46E5)) // Indigo 600
                        .border(4.dp, Color(0xFF0B0C15), CircleShape)
                        .clickable { /* TODO */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DarkMode,
                        contentDescription = "Moon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                NavButton(icon = Icons.Outlined.FavoriteBorder, contentDescription = "Favorites")
                NavButton(icon = Icons.Outlined.PersonOutline, contentDescription = "Profile")
            }
        }
    }
}

@Composable
fun NavButton(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable { /* TODO */ },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color(0xFF9CA3AF), // Gray 400
            modifier = Modifier.size(24.dp)
        )
    }
}
