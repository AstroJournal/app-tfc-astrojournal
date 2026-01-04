package com.app.astrojournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.astrojournal.eventdetail.EventDetailViewModel
import com.app.astrojournal.model.Event

@Composable
fun EventDetailScreen(
    event: Event,
    viewModel: EventDetailViewModel
) {
    val collectibles = viewModel.collectibles
    var notes by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Fondo temporal (hasta que tengas imágenes)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A0F)) // fondo negro
        )

        // Capa oscura encima (como en tu diseño)
        Image(
            painter = painterResource(id = event.planetImageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )


        // Cuadro transparente con detalles
        Column( modifier = Modifier .align(Alignment.Center)
            .clip(RoundedCornerShape(32.dp)) // ← BORDES CURVOS
            .background(Color.White.copy(alpha = 0.12f)) // ← TRANSPARENTE REAL
            .shadow(12.dp, RoundedCornerShape(32.dp)) // ← EFECTO 3D
            .padding(24.dp) ) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                text = event.dateTime,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                viewModel.eventoObservado(event.id, notes)
                notes = ""
            }) {
                Text("Marcar como observado")
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Mis notas:",
                color = Color.White
            )

            collectibles.forEach { item ->
                if (item.eventId == event.id) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "• ${item.notes}",
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = { viewModel.borrarNota(item.id) },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Borrar")

                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }



        }
    }
}
