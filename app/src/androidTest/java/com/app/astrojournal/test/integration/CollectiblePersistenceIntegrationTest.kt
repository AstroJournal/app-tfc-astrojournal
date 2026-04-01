package com.app.astrojournal.test.integration

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.shared.data.db.CollectibleRepository
import com.app.astrojournal.ui.viewmodels.EventDetailViewModel
import com.astrojournal.shared.data.db.AstrojournalDatabase
import com.astrojournal.shared.data.db.DatabaseDriverFactory
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CollectiblePersistenceIntegrationTest {

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    private lateinit var repo: CollectibleRepository

    @Before
    fun setUp() {
        repo = createRepository()
        repo.getAll().forEach { repo.deleteById(it.id) }
    }

    @Test
    fun sqlite_saveReadUpdateEventObservedAndNotes() {
        repo.insertCollectible(
            eventId = 501L,
            eventName = "Solar Eclipse",
            observationDate = "2026-08-12",
            notes = "Initial note",
            observed = 0,
            agended = 1
        )

        val created = repo.getAll().firstOrNull { it.eventId == 501L }
        assertNotNull(created)

        val id = created!!.id
        repo.updateObserved(id, 1)
        repo.updateNotes(id, "Updated note")

        val updated = repo.getById(id)
        assertNotNull(updated)
        assertEquals(1L, updated!!.observed)
        assertEquals("Updated note", updated.notes)
    }

    @Test
    fun sqlite_dataPersistsWhenRepositoryIsRecreated() {
        repo.insertCollectible(
            eventId = 900L,
            eventName = "Lunar Eclipse",
            observationDate = "2027-03-01",
            notes = "Persistent note",
            observed = 1,
            agended = 0
        )

        val repo2 = createRepository()
        val row = repo2.getAll().firstOrNull { it.eventId == 900L }

        assertNotNull(row)
        assertEquals("Persistent note", row!!.notes)
        assertEquals(1L, row.observed)
    }

    @Test
    fun sqlite_deleteRemovesNoteAndEntry() {
        repo.insertCollectible(
            eventId = 777L,
            eventName = "Venus Event",
            observationDate = "2027-05-09",
            notes = "To be deleted",
            observed = 0,
            agended = 0
        )

        val row = repo.getAll().first { it.eventId == 777L }
        repo.deleteById(row.id)

        assertNull(repo.getById(row.id))
        assertTrue(repo.getAll().none { it.eventId == 777L })
    }

    @Test
    fun sqlite_viewModelSaveAvoidsDuplicatesForSameEventId() = runBlocking {
        val viewModel = EventDetailViewModel(repo)

        viewModel.saveFullEventState(
            eventId = 321L,
            eventName = "Saturn",
            date = "2027-09-01",
            note = "first",
            agended = true,
            observed = false
        )

        viewModel.saveFullEventState(
            eventId = 321L,
            eventName = "Saturn",
            date = "2027-09-01",
            note = "updated",
            agended = false,
            observed = true
        )

        val rows = waitForRows(eventId = 321L)

        assertEquals(1, rows.size)
        assertEquals("updated", rows.first().notes)
        assertEquals(1L, rows.first().observed)
    }

    private fun createRepository(): CollectibleRepository {
        val driver = DatabaseDriverFactory(context).createDriver()
        val database = AstrojournalDatabase(driver)
        return CollectibleRepository(database.collectibleQueries)
    }

    private fun waitForRows(eventId: Long): List<com.astrojournal.shared.data.db.Collectible> {
        repeat(40) {
            val rows = repo.getAll().filter { it.eventId == eventId }
            if (rows.isNotEmpty()) return rows
            Thread.sleep(50)
        }
        return repo.getAll().filter { it.eventId == eventId }
    }
}
