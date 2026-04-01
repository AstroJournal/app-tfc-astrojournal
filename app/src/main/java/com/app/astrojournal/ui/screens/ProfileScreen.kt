package com.app.astrojournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.app.astrojournal.R
import com.app.astrojournal.ui.components.AstroBottomNavigation

@Composable
fun ProfileScreen(
    currentScreen: String = "profile",
    onNavigate: (String) -> Unit = {}
) {
    Scaffold(
        bottomBar = { AstroBottomNavigation(currentScreen = currentScreen, onNavigate = onNavigate) },
        containerColor = Color.Transparent,
        contentColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BackgroundDark, BackgroundGradientEnd)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.stary_night_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.6f),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "my profile",
                style = MaterialTheme.typography.headlineMedium.copy(color = Color.White)
            )
        }
    }
}
