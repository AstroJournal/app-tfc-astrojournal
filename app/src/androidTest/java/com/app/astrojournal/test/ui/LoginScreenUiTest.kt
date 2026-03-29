package com.app.astrojournal.test.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.app.astrojournal.ui.screens.LoginScreen
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun login_withValidEmail_triggersSuccessCallback() {
        var loginSuccessCalled = false

        composeRule.setContent {
            LoginScreen(
                onLoginSuccess = { loginSuccessCalled = true },
                onNavigateToRegister = {}
            )
        }

        composeRule.onNodeWithTag("login_email_input").performTextInput("astro@test.com")
        composeRule.onNodeWithTag("login_password_input").performTextInput("123456")
        composeRule.onNodeWithTag("login_submit_button").performClick()

        assertTrue(loginSuccessCalled)
    }

    @Test
    fun login_withInvalidEmail_showsValidationError() {
        var loginSuccessCalled = false

        composeRule.setContent {
            LoginScreen(
                onLoginSuccess = { loginSuccessCalled = true },
                onNavigateToRegister = {}
            )
        }

        composeRule.onNodeWithTag("login_email_input").performTextInput("email_invalido")
        composeRule.onNodeWithTag("login_password_input").performTextInput("123456")
        composeRule.onNodeWithTag("login_submit_button").performClick()

        composeRule.onNodeWithText("Formato de email inválido").assertIsDisplayed()
        assertFalse(loginSuccessCalled)
    }
}
