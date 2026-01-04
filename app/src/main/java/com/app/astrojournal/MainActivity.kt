package com.app.astrojournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.astrojournal.ui.screens.EventDetailScreen
import com.app.shared.data.db.CollectibleRepository
import db.AstrojournalDatabase
import com.app.astrojournal.eventdetail.EventDetailViewModel
import com.astrojournal.shared.data.db.DatabaseDriverFactory
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.app.astrojournal.model.Event
import com.app.astrojournal.ui.screens.HomeScreen
import com.app.astrojournal.ui.theme.AstrojournalTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AstrojournalTheme {
                HomeScreen()
            }

        // Crear el driver de SQLDelight (Android)
        val driverFactory = DatabaseDriverFactory(this)
        val driver = driverFactory.createDriver()

        // Crear la base de datos generada por SQLDelight
        val database = AstrojournalDatabase(driver)

        // Obtener los queries generados (CollectibleQueries)
        val queries = database.collectibleQueries

        // Crear el repositorio (capa de datos)
        val repo = CollectibleRepository(queries)

        // Crear el ViewModel (capa de presentación)
        val viewModel = EventDetailViewModel(repo)

        //Setear colores
        val astroColors = darkColorScheme(
            primary = Color(0xFFBB86FC),
            onPrimary = Color.Black,
            background = Color(0xFF121212),
            onBackground = Color(0xFFE0E0E0),
            surface = Color(0xFF1E1E1E),
            onSurface = Color(0xFFE0E0E0)
        )

        //Evento de prueba
        val fakeEvent = Event(
            id = "e1",
            name = "Pruebitaaaaa",
            dateTime = "2026-01-05 22:00",
            planetImageRes = R.drawable.moon // img prueba
        )

        // Iniciar Compose en pantalla
        setContent { // Forzar tema oscuro
            MaterialTheme(
                colorScheme = astroColors
            )
            {
                EventDetailScreen(
                    event = fakeEvent,
                    viewModel = viewModel
                )

            }
        }
    }
}
