package com.app.astrojournal.test.unit.fakes

import com.app.shared.data.db.CollectibleStore
import com.astrojournal.shared.data.db.Collectible

class FakeCollectibleStore : CollectibleStore {
    private val rows = mutableListOf<Collectible>()
    private var idCounter = 1L

    override fun insertCollectible(
        eventId: Long,
        eventName: String,
        observationDate: String,
        notes: String?,
        observed: Int,
        agended: Int
    ) {
        rows.add(
            Collectible(
                id = idCounter++,
                eventId = eventId,
                eventName = eventName,
                observationDate = observationDate,
                notes = notes,
                observed = observed.toLong(),
                agended = agended.toLong()
            )
        )
    }

    override fun updateObserved(id: Long, observed: Int) {
        updateById(id) { row -> row.copy(observed = observed.toLong()) }
    }

    override fun updateNotes(id: Long, notes: String?) {
        updateById(id) { row -> row.copy(notes = notes) }
    }

    override fun updateNotesByEventId(eventId: Long, notes: String?) {
        val index = rows.indexOfFirst { it.eventId == eventId }
        if (index >= 0) {
            rows[index] = rows[index].copy(notes = notes)
        }
    }

    override fun updateAgended(id: Long, agended: Int) {
        updateById(id) { row -> row.copy(agended = agended.toLong()) }
    }

    override fun getAll(): List<Collectible> = rows.toList()

    override fun getById(id: Long): Collectible? = rows.firstOrNull { it.id == id }

    override fun getByEventId(eventId: Long): Collectible? =
        rows.lastOrNull { it.eventId == eventId }

    override fun deleteById(id: Long) {
        rows.removeAll { it.id == id }
    }

    override fun deleteByEventId(eventId: Long) {
        rows.removeAll { it.eventId == eventId }
    }

    private fun updateById(id: Long, update: (Collectible) -> Collectible) {
        val index = rows.indexOfFirst { it.id == id }
        if (index >= 0) {
            rows[index] = update(rows[index])
        }
    }
}
