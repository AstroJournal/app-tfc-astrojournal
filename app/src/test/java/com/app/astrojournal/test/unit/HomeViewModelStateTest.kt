package com.app.astrojournal.test.unit

import com.app.astrojournal.data.model.AstroEvent
import com.app.astrojournal.data.model.EventType
import com.app.astrojournal.ui.viewmodels.HomeViewModel
import com.app.astrojournal.ui.viewmodels.UiState
import com.app.astrojournal.utils.MoonPhaseInfo
import io.github.cosinekitty.astronomy.Time
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelStateTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Test
    fun fetchMoonData_ordersEventsByTimestamp() = runTest {
        val unsortedEvents = listOf(
            AstroEvent("Late", "", "", 300L, EventType.OTHER),
            AstroEvent("Early", "", "", 100L, EventType.OTHER),
            AstroEvent("Middle", "", "", 200L, EventType.OTHER)
        )

        val vm = HomeViewModel(
            moonPhaseProvider = { MoonPhaseInfo(1.0, 10, "New Moon", Date()) },
            upcomingEventsProvider = { _: Time -> unsortedEvents },
            loadingDelayMs = 0
        )

        vm.fetchMoonData()
        advanceUntilIdle()

        assertEquals(listOf(100L, 200L, 300L), vm.upcomingEvents.value.map { it.timestamp })
        assertTrue(vm.uiState.value is UiState.Success)
    }

    @Test
    fun fetchMoonData_whenSourceFails_setsErrorState() = runTest {
        val vm = HomeViewModel(
            moonPhaseProvider = { throw RuntimeException("API down") },
            loadingDelayMs = 0
        )

        vm.fetchMoonData()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is UiState.Error)
        assertTrue((state as UiState.Error).message.contains("API down"))
    }

    @Test
    fun fetchMoonData_whenNoEvents_keepsEmptyListAndSuccess() = runTest {
        val vm = HomeViewModel(
            moonPhaseProvider = { MoonPhaseInfo(10.0, 70, "Waxing Gibbous", Date()) },
            upcomingEventsProvider = { _: Time -> emptyList() },
            loadingDelayMs = 0
        )

        vm.fetchMoonData()
        advanceUntilIdle()

        assertTrue(vm.uiState.value is UiState.Success)
        assertTrue(vm.upcomingEvents.value.isEmpty())
    }
}
