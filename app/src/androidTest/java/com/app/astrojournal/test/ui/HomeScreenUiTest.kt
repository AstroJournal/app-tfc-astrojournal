package com.app.astrojournal.test.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import com.app.astrojournal.data.model.AstroEvent
import com.app.astrojournal.data.model.EventType
import com.app.astrojournal.ui.screens.HomeScreen
import com.app.astrojournal.ui.viewmodels.HomeViewModel
import com.app.astrojournal.utils.MoonPhaseInfo
import io.github.cosinekitty.astronomy.Time
import org.junit.Rule
import org.junit.Test
import java.util.Date

class HomeScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun home_displaysLoadingState() {
        val vm = HomeViewModel(
            moonPhaseProvider = { MoonPhaseInfo(1.0, 2, "New Moon", Date()) },
            upcomingEventsProvider = { _: Time -> emptyList() },
            loadingDelayMs = 10_000
        )

        composeRule.setContent {
            HomeScreen(viewModel = vm)
        }

        composeRule.onNodeWithTag("home_loading_indicator").assertIsDisplayed()
    }

    @Test
    fun home_displaysSuccessStateWithEvents() {
        val vm = HomeViewModel(
            moonPhaseProvider = { MoonPhaseInfo(2.0, 10, "Waxing Crescent", Date()) },
            upcomingEventsProvider = {
                listOf(
                    AstroEvent("Event 1", "desc", "today", 1L, EventType.OTHER)
                )
            },
            loadingDelayMs = 0
        )

        composeRule.setContent {
            HomeScreen(viewModel = vm)
        }

        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithTag("event_item_0").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("event_item_0").assertIsDisplayed()
    }

    @Test
    fun home_displaysErrorStateWhenProviderFails() {
        val vm = HomeViewModel(
            moonPhaseProvider = { throw RuntimeException("network error") },
            loadingDelayMs = 0
        )

        composeRule.setContent {
            HomeScreen(viewModel = vm)
        }

        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithTag("home_error_title").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("home_error_title").assertIsDisplayed()
    }

    @Test
    fun home_displaysEmptyMessageWhenNoEvents() {
        val vm = HomeViewModel(
            moonPhaseProvider = { MoonPhaseInfo(5.0, 30, "First Quarter", Date()) },
            upcomingEventsProvider = { _: Time -> emptyList() },
            loadingDelayMs = 0
        )

        composeRule.setContent {
            HomeScreen(viewModel = vm)
        }

        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithTag("empty_events_message").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("empty_events_message").assertIsDisplayed()
    }
}
