package com.app.astrojournal.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.astrojournal.di.AppModule
import com.app.astrojournal.eventdetail.EventDetailRoute
import com.app.astrojournal.ui.theme.AstrojournalTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppModule.init(this)

        setContent {
            AstrojournalTheme {
                EventDetailRoute(eventId = 1L)   // ← TU EVENTO
            }
        }
    }
}
