package com.app.astrojournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.astrojournal.R
import com.app.astrojournal.data.model.ApodUi
import com.app.astrojournal.data.model.VisibilityUi
import com.app.astrojournal.ui.components.AstroBottomNavigation
import com.app.astrojournal.ui.viewmodels.EventOfTheDayViewModel
import com.app.astrojournal.ui.viewmodels.RemoteUiState
import coil.compose.AsyncImage
import android.os.SystemClock

@Composable
fun EventOfTheDayScreen(
    viewModel: EventOfTheDayViewModel,
    currentScreen: String = "eventOfTheDay",
    onNavigate: (String) -> Unit = {}
) {
    EventOfTheDayContent(
        apodState = viewModel.apodState,
        visibilityState = viewModel.visibilityState,
        onRetry = { viewModel.load() },
        currentScreen = currentScreen,
        onNavigate = onNavigate
    )
}

@Composable
fun EventOfTheDayContent(
    apodState: RemoteUiState<ApodUi>,
    visibilityState: RemoteUiState<VisibilityUi>,
    onRetry: () -> Unit,
    currentScreen: String = "eventOfTheDay",
    onNavigate: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { com.app.astrojournal.ui.components.AstroTopBar(onProfileClick = { onNavigate("profile") }) },
        bottomBar = {
            AstroBottomNavigation(currentScreen = currentScreen, onNavigate = onNavigate)
        },
        containerColor = Color.Transparent,
        contentColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.stary_night_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                EventOfDayVisibilitySection(
                    state = visibilityState
                )

                Spacer(modifier = Modifier.height(12.dp))

                EventOfDayApodSection(
                    state = apodState,
                    onRetry = onRetry
                )
            }
        }
    }
}

@Composable
private fun EventOfDayApodSection(
    state: RemoteUiState<ApodUi>,
    onRetry: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    var lastOpenTs by remember { mutableStateOf(0L) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 520.dp)
            .testTag("event_of_day_apod_section")
            .clip(RoundedCornerShape(18.dp))
            .background(GlassPanelBg)
            .border(1.dp, White10, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Today's Historical Event",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFA5B4FC),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("event_of_day_title")
        )
        Spacer(modifier = Modifier.height(8.dp))

        when (state) {
            is RemoteUiState.Loading -> {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color(0xFF818CF8))
                    Text("Loading today's image...", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.bodySmall)
                }
            }

            is RemoteUiState.Error -> {
                Text("Failed to load today's event.", color = Color(0xFFCBD5E1))
                TextButton(onClick = onRetry) { Text("Retry") }
            }

            is RemoteUiState.Success -> {
                val data = state.data
                val hasLink = data.imageUrl.isNotBlank()

                fun openLinkSafely() {
                    if (!hasLink) return
                    val now = SystemClock.elapsedRealtime()
                    if (now - lastOpenTs < 1200L) return
                    lastOpenTs = now
                    runCatching { uriHandler.openUri(data.imageUrl) }
                }

                if (hasLink) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = data.imageUrl,
                            contentDescription = data.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Image not available", color = Color(0xFF94A3B8), style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    data.title,
                    color = Color(0xFFA5B4FC),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    data.description,
                    color = Color(0xFFCBD5E1),
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )

                if (hasLink) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { openLinkSafely() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1), contentColor = Color.White)
                    ) {
                        Text("Open in browser")
                    }
                }
            }
        }
    }
}

@Composable
private fun EventOfDayVisibilitySection(
    state: RemoteUiState<VisibilityUi>
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("event_of_day_visibility_section")
            .clip(RoundedCornerShape(18.dp))
            .background(GlassPanelBg)
            .border(1.dp, White10, RoundedCornerShape(18.dp))
            .clickable { expanded = !expanded }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Visibility", color = Color(0xFFA5B4FC), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
            androidx.compose.material3.Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = Color(0xFF818CF8)
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Visibility details",
                color = Color(0xFFCBD5E1),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("event_of_day_visibility_details")
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .padding(12.dp)
            ) {
                when (state) {
                    is RemoteUiState.Loading -> Text(
                        "Calculating...",
                        color = Color(0xFFCBD5E1),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    is RemoteUiState.Error -> Text(
                        "Information not available",
                        color = Color(0xFF94A3B8),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    is RemoteUiState.Success -> {
                        val data = state.data
                        Column {
                            Text(
                                if (data.isObservable) "Observable" else "Limited visibility",
                                color = Color(0xFF818CF8),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Window: ${data.window}",
                                color = Color(0xFFCBD5E1),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                data.message,
                                color = Color(0xFFCBD5E1),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
