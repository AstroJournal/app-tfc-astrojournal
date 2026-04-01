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
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * Custom Bottom Navigation Bar with navigation support.
 * @param currentScreen The currently selected screen identifier.
 * @param onNavigate Callback invoked when a navigation button is tapped.
 */
@Composable
fun AstroBottomNavigation(
    currentScreen: String = "home",
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp)
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
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .height(64.dp)
                .fillMaxWidth(0.85f)
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
                NavButton(
                    icon = Icons.Outlined.PeopleOutline,
                    contentDescription = "Social",
                    isSelected = currentScreen == "social",
                    onClick = { onNavigate("social") }
                )
                NavButton(
                    icon = Icons.Outlined.CalendarToday,
                    contentDescription = "Calendar",
                    isSelected = currentScreen == "calendar",
                    onClick = { onNavigate("calendar") },
                    modifier = Modifier.testTag("bottom_nav_calendar")
                )
                NavButton(
                    icon = Icons.Default.DarkMode,
                    contentDescription = "Home",
                    isSelected = currentScreen == "home",
                    onClick = { onNavigate("home") }
                )
                NavButton(
                    icon = Icons.Outlined.Visibility,
                    contentDescription = "Observed",
                    isSelected = currentScreen == "observed",
                    onClick = { onNavigate("observed") }
                )
                NavButton(
                    icon = Icons.Outlined.FormatListBulleted,
                    contentDescription = "Agenda",
                    isSelected = currentScreen == "eventDetail",
                    onClick = { onNavigate("eventDetail") },
                    modifier = Modifier.testTag("bottom_nav_event_detail")
                )
            }
        }
    }
}

@Composable
fun NavButton(
    icon: ImageVector,
    contentDescription: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val tintColor = if (isSelected) Color(0xFF6366F1) else Color(0xFF9CA3AF) // Indigo 500 vs Gray 400

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .then(
                if (isSelected) Modifier.background(Color(0xFF6366F1).copy(alpha = 0.15f))
                else Modifier
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tintColor,
            modifier = Modifier.size(24.dp)
        )
    }
}
