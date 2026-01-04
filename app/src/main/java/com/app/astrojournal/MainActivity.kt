package com.app.astrojournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.app.astrojournal.ui.screens.HomeScreen
import com.app.astrojournal.ui.theme.AstrojournalTheme
import com.app.astrojournal.ui.theme.AstrojournalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AstrojournalTheme {
                HomeScreen()
            }
        }
    }
}

