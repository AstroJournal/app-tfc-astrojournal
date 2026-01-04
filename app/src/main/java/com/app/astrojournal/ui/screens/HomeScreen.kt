package com.app.astrojournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.North
import androidx.compose.material.icons.rounded.NorthEast
import androidx.compose.material.icons.rounded.South
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.astrojournal.ui.components.AstroBottomNavigation
import androidx.compose.ui.res.painterResource
import com.app.astrojournal.R

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



/**
 * Main Home Screen Composable.
 * Implements a starry background with a radial gradient effect.
 * Uses a Scaffold to position the custom bottom navigation.
 */
@Composable
fun HomeScreen() {
    Scaffold(
        bottomBar = { AstroBottomNavigation() },
        containerColor = BackgroundDark,
        contentColor = Color.White
    ) { innerPadding ->
        // Main container with custom background gradient and image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BackgroundDark, BackgroundGradientEnd)
                    )
                )
        ) {
            // Background Image (Starry/Cloudy texture)
            Image(
                painter = painterResource(id = R.drawable.stary_night_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.6f), // Adjusted opacity for the starry night image
                contentScale = ContentScale.Crop
            )

            // Scrollable content area
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding), // Use scaffold padding
                contentPadding = PaddingValues(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MoonHeader()
                }
                item {
                    ComingWeek()
                }
                item {
                    CelestialObjectsList()
                }
            }
        }
    }
}

/**
 * Header section displaying the current Moon image, name, and age.
 * Uses a blur effect behind the moon to create a "glow".
 */
@Composable
fun MoonHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
    ) {
        // Moon Image Container with Glow
        Box(
            modifier = Modifier
                .size(256.dp) // w-64 h-64
                .padding(bottom = 16.dp)
        ) {
            // Local Moon Image
            Image(
                painter = painterResource(id = R.drawable.moon_full),
                contentDescription = "Full Moon",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        // Moon Name
        Text(
            text = "Full Moon",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = TextGray100
            )
        )
        // Moon Age info
        Text(
            text = "Moon Age: 14.8 days", // Updated for full moon
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Indigo200,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
        
        // Illumination Badge
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .background(Color(0xFF312E81).copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .border(1.dp, Indigo500.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "Illumination: 100%",
                style = MaterialTheme.typography.labelSmall.copy(color = TextGray400)
            )
        }
    }
}

/**
 * Section showing the upcoming moon phases for the week.
 * Styled as a "glass" panel.
 * Displays a 7-day forecast.
 */
@Composable
fun ComingWeek() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassPanelBg) // Glass effect background
            .border(1.dp, White10, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "7-DAY FORECAST",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
            )
            Text(
                text = "View All",
                style = MaterialTheme.typography.labelSmall.copy(color = Indigo300),
                modifier = Modifier.clickable { }
            )
        }

        // Days Grid - using Row for now, but 7 items might need a LazyRow or scrollable Row if screen is small
        // For standard width, 7 items should fit if small enough, but let's use a scrollable Row to be safe/modern with 7 items
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
             // Mock data representing next 7 days from Full Moon
            DayItem(day = "Mon", type = MoonPhaseType.Full, label = "Full")
            DayItem(day = "Tue", type = MoonPhaseType.Regular(16), label = "16d", phaseName = "Waning Gibbous")
            DayItem(day = "Wed", type = MoonPhaseType.Regular(17), label = "17d", phaseName = "Waning Gibbous")
            DayItem(day = "Thu", type = MoonPhaseType.Regular(18), label = "18d", phaseName = "Waning Gibbous")
            DayItem(day = "Fri", type = MoonPhaseType.Regular(19), label = "19d", phaseName = "Waning Gibbous")
            DayItem(day = "Sat", type = MoonPhaseType.Regular(20), label = "20d", phaseName = "Last Quarter")
            DayItem(day = "Sun", type = MoonPhaseType.Regular(21), label = "21d", phaseName = "Last Quarter")
        }
    }
}

sealed class MoonPhaseType {
    data class Regular(val day: Int) : MoonPhaseType()
    data object Full : MoonPhaseType()
}

@Composable
fun DayItem(
    day: String, 
    type: MoonPhaseType,
    label: String,
    phaseName: String = "Full" // Default value, but intended to be passed explicitly for forecast
) {
    // Column layout for individual day item in the forecast row
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable { }
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = Indigo200,
                fontWeight = FontWeight.Medium
            )
        )
        
        // Icon Container
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.Transparent), // No background needed for PNG icons usually
            contentAlignment = Alignment.Center
        ) {
            // Determine the icon resource ID based on the phase name using helper function
            val iconResId = getMoonPhaseIcon(if (type is MoonPhaseType.Full) "Full" else phaseName)
            
            // Display the moon phase icon
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = phaseName,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = if (type is MoonPhaseType.Full) Color.White else TextGray400,
                fontWeight = if (type is MoonPhaseType.Full) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

/**
 * Conditional function to assign one of the attached icons to its corresponding phase.
 * Maps string phase names to drawable resource IDs.
 * 
 * @param phaseName The name of the moon phase (e.g., "Full", "Waning Gibbous")
 * @return The resource ID of the matching drawable
 */
fun getMoonPhaseIcon(phaseName: String): Int {
    return when (phaseName) {
        // Primary phases mapping
        "New Moon" -> R.drawable.ic_moon_new
        "First Quarter" -> R.drawable.ic_moon_first_quarter
        "Full" -> R.drawable.ic_moon_full
        "Waning Gibbous" -> R.drawable.ic_moon_waning_gibbous
        "Last Quarter" -> R.drawable.ic_moon_last_quarter
        
        // Fallbacks for phases not explicitly covered by the 5 icons provided
        // Logic aims to provide the closest visual approximation
        "Waxing Gibbous" -> R.drawable.ic_moon_first_quarter 
        "Waxing Crescent" -> R.drawable.ic_moon_first_quarter 
        "Waning Crescent" -> R.drawable.ic_moon_last_quarter 
        
        // Default fallback to Full moon if name doesn't match
        else -> R.drawable.ic_moon_full
    }
}


@Composable
fun CelestialObjectsList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassPanelBg)
            .border(1.dp, White10, RoundedCornerShape(16.dp))
    ) {
        CelestialItem(
            name = "Ursa Major",
            description = "The Big Bear, The Big Dipper",
            properties = listOf("Elev: 4.2°" to null),
            icon = { StarIconShape(color = Indigo300) }
        )
        HorizontalDivider()
        CelestialItem(
            name = "Sirius",
            description = "HIP 32349, HR 2491, HD 48915",
            properties = listOf("27.1°" to Icons.Rounded.NorthEast, "16:23" to Icons.Rounded.South, "00:57" to Icons.Rounded.North),
            icon = { Icon(Icons.Rounded.Star, contentDescription = null, tint = Indigo300, modifier = Modifier.size(24.dp)) }
        )
        HorizontalDivider()
        CelestialItem(
            name = "Venus",
            description = "2nd Planet",
            properties = listOf("10°" to Icons.Rounded.NorthEast, "01:26" to Icons.Rounded.South, "17:19" to Icons.Rounded.North),
            icon = { PlanetIcon() }
        )
        HorizontalDivider()
        CelestialItem(
            name = "C/2019 U6 (Lemmon)",
            description = "Comet",
            properties = listOf("Elev: 38.4°" to Icons.Rounded.NorthEast),
            icon = { CometIcon() }
        )
    }
}

@Composable
fun HorizontalDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(White10)
    )
}

@Composable
fun CelestialItem(
    name: String,
    description: String,
    properties: List<Pair<String, ImageVector?>>,
    icon: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Indigo500.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(color = TextGray400),
                maxLines = 1
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                properties.forEach { (text, iconVec) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (iconVec != null) {
                            Icon(
                                imageVector = iconVec, 
                                contentDescription = null, 
                                tint = if(iconVec == Icons.Rounded.NorthEast) Indigo300 else TextGray400,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = if(iconVec == Icons.Rounded.NorthEast || text.contains("Elev")) Indigo300 else TextGray400
                            )
                        )
                    }
                }
            }
        }
        
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = "Detail",
            tint = Color.Gray
        )
    }
}

// Placeholder composables for custom SVG shapes
@Composable
fun StarIconShape(color: Color) {
    // Simplified representation
     Icon(
        imageVector = Icons.Rounded.Star, // Fallback
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
        contentAlignment = Alignment.Center
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
        imageVector = Icons.Rounded.Star, // Fallback
        contentDescription = null,
        tint = Indigo300
    )
}
