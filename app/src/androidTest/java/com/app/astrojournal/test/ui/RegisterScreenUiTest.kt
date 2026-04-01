package com.app.astrojournal.test.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.app.astrojournal.ui.screens.RegisterScreen
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RegisterScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun register_withValidData_triggersSuccessCallback() {
        var registerSuccessCalled = false

        composeRule.setContent {
            RegisterScreen(
                onRegisterSuccess = { registerSuccessCalled = true },
                onNavigateToLogin = {}
            )
        }

        composeRule.onNodeWithTag("register_username_input").performTextInput("astrofan")
        composeRule.onNodeWithTag("register_email_input").performTextInput("astro@test.com")
        composeRule.onNodeWithTag("register_password_input").performTextInput("123456")
        composeRule.onNodeWithTag("register_repeat_password_input").performTextInput("123456")
        composeRule.onNodeWithTag("register_submit_button").performClick()

        assertTrue(registerSuccessCalled)
    }

    @Test
    fun register_withInvalidData_showsValidationErrors() {
        var registerSuccessCalled = false

        composeRule.setContent {
            RegisterScreen(
                onRegisterSuccess = { registerSuccessCalled = true },
                onNavigateToLogin = {}
            )
        }

        composeRule.onNodeWithTag("register_username_input").performTextInput("astrofan")
        composeRule.onNodeWithTag("register_email_input").performTextInput("email_invalido")
        composeRule.onNodeWithTag("register_password_input").performTextInput("123456")
        composeRule.onNodeWithTag("register_repeat_password_input").performTextInput("654321")
        composeRule.onNodeWithTag("register_submit_button").performClick()

        composeRule.onNodeWithText("Formato de email inválido").assertIsDisplayed()
        composeRule.onNodeWithText("Las contraseñas no coinciden o están vacías").assertIsDisplayed()
        assertFalse(registerSuccessCalled)
    }
}
