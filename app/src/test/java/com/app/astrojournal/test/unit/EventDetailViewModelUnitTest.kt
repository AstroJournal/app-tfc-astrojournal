package com.app.astrojournal.test.unit

import com.app.astrojournal.test.unit.fakes.FakeCollectibleStore
import com.app.astrojournal.ui.viewmodels.EventDetailUiState
import com.app.astrojournal.ui.viewmodels.EventDetailViewModel
import com.app.shared.data.db.CollectibleStore
import com.astrojournal.shared.data.db.Collectible
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EventDetailViewModelUnitTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Test
    fun loadEvent_loadsCorrectEventById_andSetsSuccessState() = runTest {
        val store = FakeCollectibleStore().apply {
            insertCollectible(100L, "Event A", "01 Jan", "A", observed = 1, agended = 0)
            insertCollectible(200L, "Event B", "02 Jan", "B note", observed = 0, agended = 1)
        }
        val vm = EventDetailViewModel(store, ioDispatcher = UnconfinedTestDispatcher(testScheduler))
        advanceUntilIdle()

        vm.loadEvent(200L)
        advanceUntilIdle()

        assertFalse(vm.isEventObserved)
        assertTrue(vm.isEventAgended)
        assertEquals("B note", vm.currentEventNote)
        assertTrue(vm.uiState is EventDetailUiState.Success)
    }

    @Test
    fun saveFullEventState_marksEventAsObserved() = runTest {
        val store = FakeCollectibleStore()
        val vm = EventDetailViewModel(store, ioDispatcher = UnconfinedTestDispatcher(testScheduler))
        advanceUntilIdle()

        vm.saveFullEventState(
            eventId = 777L,
            eventName = "Jupiter Opposition",
            date = "10 Oct 2026",
            note = "",
            agended = false,
            observed = true
        )
        advanceUntilIdle()
        vm.updateEventStatus(777L)

        assertTrue(vm.isEventObserved)
    }

    @Test
    fun saveFullEventState_addsAndRemovesNote() = runTest {
        val store = FakeCollectibleStore()
        val vm = EventDetailViewModel(store, ioDispatcher = UnconfinedTestDispatcher(testScheduler))
        advanceUntilIdle()

        vm.saveFullEventState(
            eventId = 888L,
            eventName = "Moon Event",
            date = "01 Jan 2027",
            note = "Bring telescope",
            agended = false,
            observed = false
        )
        advanceUntilIdle()

        vm.updateEventStatus(888L)
        assertEquals("Bring telescope", vm.currentEventNote)

        vm.saveFullEventState(
            eventId = 888L,
            eventName = "Moon Event",
            date = "01 Jan 2027",
            note = "",
            agended = false,
            observed = false
        )
        advanceUntilIdle()

        vm.updateEventStatus(888L)
        assertEquals("", vm.currentEventNote)
        assertTrue(vm.collectibles.none { it.eventId == 888L })
    }

    @Test
    fun saveFullEventState_updatesWithoutCreatingDuplicatesForSameEventId() = runTest {
        val store = FakeCollectibleStore()
        val vm = EventDetailViewModel(store, ioDispatcher = UnconfinedTestDispatcher(testScheduler))
        advanceUntilIdle()

        vm.saveFullEventState(999L, "Event", "01 Jan", "first", agended = true, observed = false)
        advanceUntilIdle()
        vm.saveFullEventState(999L, "Event", "01 Jan", "second", agended = false, observed = true)
        advanceUntilIdle()

        val matches = vm.collectibles.filter { it.eventId == 999L }
        assertEquals(1, matches.size)
        assertEquals("second", matches.first().notes)
        assertEquals(1L, matches.first().observed)
    }

    @Test
    fun loadEvent_whenRepositoryFails_setsErrorState() = runTest {
        val failingStore = object : CollectibleStore {
            override fun insertCollectible(eventId: Long, eventName: String, observationDate: String, notes: String?, observed: Int, agended: Int) = Unit
            override fun updateObserved(id: Long, observed: Int) = Unit
            override fun updateNotes(id: Long, notes: String?) = Unit
            override fun updateNotesByEventId(eventId: Long, notes: String?) = Unit
            override fun updateAgended(id: Long, agended: Int) = Unit
            override fun getAll(): List<Collectible> = emptyList()
            override fun getById(id: Long): Collectible? = null
            override fun getByEventId(eventId: Long): Collectible? = throw IllegalStateException("db down")
            override fun deleteById(id: Long) = Unit
            override fun deleteByEventId(eventId: Long) = Unit
        }

        val vm = EventDetailViewModel(failingStore, ioDispatcher = UnconfinedTestDispatcher(testScheduler))
        advanceUntilIdle()

        vm.loadEvent(123L)
        advanceUntilIdle()

        assertTrue(vm.uiState is EventDetailUiState.Error)
    }
}
