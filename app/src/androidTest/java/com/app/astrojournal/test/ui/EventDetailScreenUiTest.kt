package com.app.astrojournal.test.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.app.astrojournal.data.model.AstroEvent
import com.app.astrojournal.data.model.EventType
import com.app.astrojournal.test.ui.fakes.FakeCollectibleStore
import com.app.astrojournal.ui.screens.EventDetailScreen
import com.app.astrojournal.ui.viewmodels.EventDetailViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class EventDetailScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val sampleEvent = AstroEvent(
        name = "Jupiter Opposition",
        description = "Best visibility",
        date = "2026-10-10",
        timestamp = 1_800_000_000_000L,
        type = EventType.PLANET
    )

    @Test
    fun detail_showsCorrectEventAndAllowsMarkObserved() {
        val store = FakeCollectibleStore()
        val viewModel = EventDetailViewModel(store)

        composeRule.setContent {
            EventDetailScreen(event = sampleEvent, viewModel = viewModel)
        }

        composeRule.onNodeWithTag("event_detail_title").assertIsDisplayed()
        composeRule.onNodeWithTag("event_toggle_status_button").performClick()

        composeRule.waitUntil(3_000) { store.getAll().isNotEmpty() }
        assertEquals(1L, store.getAll().first().agended)
    }

    @Test
    fun detail_allowsAddAndDeleteNote() {
        val store = FakeCollectibleStore()
        val viewModel = EventDetailViewModel(store)

        composeRule.setContent {
            EventDetailScreen(event = sampleEvent.copy(timestamp = 99L), viewModel = viewModel)
        }

        composeRule.onNodeWithTag("event_note_input").performTextInput("Bring tripod")
        composeRule.onNodeWithTag("event_save_note_button").performClick()

        composeRule.waitUntil(3_000) {
            store.getAll().firstOrNull { it.eventId == 99L }?.notes == "Bring tripod"
        }

        composeRule.onNodeWithTag("event_note_input").performTextClearance()
        composeRule.onNodeWithTag("event_save_note_button").performClick()

        composeRule.waitUntil(3_000) {
            store.getAll().none { it.eventId == 99L }
        }
        assertTrue(store.getAll().none { it.eventId == 99L })
    }
}
