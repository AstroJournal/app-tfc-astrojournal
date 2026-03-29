package com.app.astrojournal.test.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.app.astrojournal.data.model.ApodUi
import com.app.astrojournal.data.model.VisibilityUi
import com.app.astrojournal.ui.screens.EventOfTheDayContent
import com.app.astrojournal.ui.viewmodels.RemoteUiState
import org.junit.Rule
import org.junit.Test

class EventOfTheDayScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun eventOfDay_rendersApodAndExpandsVisibilityDetails() {
        composeRule.setContent {
            EventOfTheDayContent(
                apodState = RemoteUiState.Success(
                    ApodUi(
                        title = "Nebula of the Day",
                        description = "A colorful nebula.",
                        imageUrl = "",
                        mediaType = "placeholder"
                    )
                ),
                visibilityState = RemoteUiState.Success(
                    VisibilityUi(
                        isObservable = true,
                        window = "20:15 - 06:45",
                        cloudCoverPercent = 22,
                        message = "Buenas condiciones estimadas para observación"
                    )
                ),
                onRetry = {}
            )
        }

        composeRule.onNodeWithTag("event_of_day_title").assertIsDisplayed()
        composeRule.onNodeWithTag("event_of_day_apod_section").assertIsDisplayed()
        composeRule.onNodeWithTag("event_of_day_visibility_section").assertIsDisplayed()
        composeRule.onNodeWithTag("event_of_day_visibility_details").assertIsNotDisplayed()

        composeRule.onNodeWithTag("event_of_day_visibility_section").performClick()
        composeRule.onNodeWithTag("event_of_day_visibility_details").assertIsDisplayed()
    }
}
