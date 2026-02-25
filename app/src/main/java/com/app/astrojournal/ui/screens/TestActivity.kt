package com.app.astrojournal.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.astrojournal.ui.theme.AstrojournalTheme

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Aquí usamos el tema de tu proyecto
            AstrojournalTheme {
                // Llamamos a tu pantalla de calendario directamente
                CalendarScreen()
            }
        }
    }
}
