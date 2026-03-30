package com.app.astrojournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.astrojournal.R
import com.app.astrojournal.ui.components.AstroBottomNavigation
import com.app.astrojournal.ui.viewmodels.ObservedEventsViewModel
import com.astrojournal.shared.data.db.Collectible

@Composable
fun ObservedEventsScreen(
    viewModel: ObservedEventsViewModel,
    currentScreen: String = "observed",
    onNavigate: (String) -> Unit = {}
) {
    val observed = viewModel.observed.collectAsState()

    // Reload every time this screen is shown so newly observed events appear
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        topBar = { com.app.astrojournal.ui.components.AstroTopBar(onProfileClick = { onNavigate("profile") }) },
        bottomBar = { AstroBottomNavigation(currentScreen = currentScreen, onNavigate = onNavigate) },
        containerColor = BackgroundDark,
        contentColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BackgroundDark, BackgroundGradientEnd)))
        ) {
            Image(
                painter = painterResource(id = R.drawable.stary_night_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().alpha(0.6f),
                contentScale = ContentScale.Crop
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 40.dp + innerPadding.calculateTopPadding(),
                    start = 16.dp, end = 16.dp,
                    bottom = 24.dp + innerPadding.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Observed Events",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = TextGray100,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Events you have marked as observed",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextGray400)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (observed.value.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Outlined.Visibility,
                                    contentDescription = null,
                                    tint = Indigo300,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No observed events yet",
                                    style = MaterialTheme.typography.bodyLarge.copy(color = TextGray400)
                                )
                                Text(
                                    "Mark events as observed from the event detail screen",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextGray400),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(observed.value) { collectible ->
                        ObservedEventItem(collectible = collectible)
                    }
                }
            }
        }
    }
}

@Composable
fun ObservedEventItem(collectible: Collectible) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GlassPanelBg)
            .border(1.dp, White10, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Indigo500.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Visibility,
                contentDescription = null,
                tint = Indigo300,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = collectible.eventName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextGray100,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = collectible.observationDate,
                style = MaterialTheme.typography.bodySmall.copy(color = TextGray400)
            )
            collectible.notes?.let { note ->
                if (note.isNotBlank()) {
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodySmall.copy(color = Indigo200),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
