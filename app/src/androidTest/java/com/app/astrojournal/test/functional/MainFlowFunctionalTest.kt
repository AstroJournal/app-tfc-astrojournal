package com.app.astrojournal.test.functional

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.app.astrojournal.di.AppModule
import com.app.astrojournal.ui.MainActivity
import org.junit.Rule
import org.junit.Test

class MainFlowFunctionalTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun user_canLoginOpenEventDetailToggleStatusAddNoteAndNavigateToCalendar() {
        AppModule.collectibleRepository.getAll().forEach { AppModule.collectibleRepository.deleteById(it.id) }

        composeRule.onNodeWithTag("login_email_input").performTextInput("astro@test.com")
        composeRule.onNodeWithTag("login_password_input").performTextInput("123456")
        composeRule.onNodeWithTag("login_submit_button").performClick()

        composeRule.waitUntil(30_000) {
            composeRule.onAllNodesWithTag("event_item_0").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("event_item_0").performScrollTo().performClick()
        composeRule.onNodeWithTag("event_detail_screen").assertIsDisplayed()

        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithTag("event_detail_success").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("event_toggle_status_button").performClick()
        composeRule.onNodeWithTag("event_note_input").performTextInput("Functional note")
        composeRule.onNodeWithTag("event_save_note_button").performClick()

        composeRule.onNodeWithTag("bottom_nav_calendar").performClick()
        composeRule.onNodeWithTag("calendar_today_cell").assertIsDisplayed()
    }
}
