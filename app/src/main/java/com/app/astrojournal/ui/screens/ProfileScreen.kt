package com.app.astrojournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.astrojournal.R
import com.app.astrojournal.di.AppModule
import com.app.astrojournal.ui.components.AstroBottomNavigation
import com.app.astrojournal.ui.viewmodels.ObservedEventsViewModel

@Composable
fun ProfileScreen(
    viewModel: ObservedEventsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ObservedEventsViewModel(AppModule.collectibleRepository) as T
            }
        }
    ),
    username: String = "Astro User",
    email: String = "usuario@ejemplo.com",
    password: String = "",
    onUpdatePassword: (String) -> Unit = {},
    currentScreen: String = "profile",
    onNavigate: (String) -> Unit = {}
) {
    val observedEvents by viewModel.observed.collectAsState()
    val count = observedEvents.size
    val scrollState = rememberScrollState() // Estado para el scroll

    var isEditingPassword by remember { mutableStateOf(false) }
    var passwordText by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val (rank, rankColor) = when {
        count <= 5 -> "Casual Observer" to Indigo200
        count in 6..10 -> "Frequent Observer" to Indigo300
        else -> "Veteran Observer" to Color(0xFFFCD34D)
    }

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
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.stary_night_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.6f),
                contentScale = ContentScale.Crop
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 40.dp,
                    bottom = innerPadding.calculateBottomPadding() + 32.dp,
                    start = 24.dp,
                    end = 24.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(GlassPanelBg)
                            .border(2.dp, Indigo500.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Indigo300
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(GlassPanelBg)
                            .border(1.dp, White10, RoundedCornerShape(24.dp))
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = username,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextGray100
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(Icons.Default.Email, null, modifier = Modifier.size(14.dp), tint = TextGray400)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextGray400)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Observed events: $count",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextGray400)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(rankColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .border(1.dp, rankColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Stars, null, tint = rankColor, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = rank.uppercase(), style = MaterialTheme.typography.labelLarge.copy(color = rankColor, fontWeight = FontWeight.ExtraBold))
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Seguridad", style = MaterialTheme.typography.labelLarge.copy(color = Indigo300, fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(12.dp))

                            if (!isEditingPassword) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(White10, RoundedCornerShape(12.dp))
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Contraseña actual", style = MaterialTheme.typography.labelSmall, color = TextGray400)
                                        Text(
                                            text = if (showPassword) password.ifEmpty { "(No disponible)" } else "••••••••",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.White
                                        )
                                    }
                                    Row {
                                        IconButton(onClick = { showPassword = !showPassword }) {
                                            Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = Indigo300)
                                        }
                                        IconButton(onClick = { isEditingPassword = true }) {
                                            Icon(Icons.Default.Edit, null, tint = Indigo300)
                                        }
                                    }
                                }
                            } else {
                                OutlinedTextField(
                                    value = passwordText,
                                    onValueChange = { passwordText = it },
                                    label = { Text("Nueva Contraseña", color = TextGray400) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Indigo500, unfocusedBorderColor = White10, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { if (passwordText.length >= 6) { onUpdatePassword(passwordText); isEditingPassword = false; passwordText = "" } }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Indigo500)) { Text("Guardar") }
                                    TextButton(onClick = { isEditingPassword = false }, modifier = Modifier.weight(1f)) { Text("Cancelar", color = TextGray400) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
