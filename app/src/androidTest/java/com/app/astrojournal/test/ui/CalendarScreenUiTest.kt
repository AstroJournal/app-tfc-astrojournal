package com.app.astrojournal.test.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.app.astrojournal.ui.screens.CalendarScreen
import org.junit.Rule
import org.junit.Test

class CalendarScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun calendar_highlightsTodayCell() {
        composeRule.setContent {
            CalendarScreen()
        }

        composeRule.onNodeWithTag("calendar_today_cell").assertIsDisplayed()
    }
}
