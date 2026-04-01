package com.app.shared.data.db

import com.astrojournal.shared.data.db.Collectible
import com.astrojournal.shared.data.db.CollectibleQueries

class CollectibleRepository(
    private val queries: CollectibleQueries
) : CollectibleStore {

    override fun insertCollectible(
        eventId: Long,                 // ← CAMBIO: antes String
        eventName: String,
        observationDate: String,
        notes: String?,
        observed: Int,
        agended: Int
    ) {
        queries.insertCollectible(
            eventId = eventId,
            eventName = eventName,
            observationDate = observationDate,
            notes = notes,
            observed = observed.toLong(),
            agended = agended.toLong()
        )
    }

    override fun updateObserved(
        id: Long,
        observed: Int
    ) {
        queries.updateObserved(
            observed = observed.toLong(),
            id = id
        )
    }

    override fun updateNotes(
        id: Long,
        notes: String?
    ) {
        queries.updateNotes(
            notes = notes,
            id = id
        )
    }

    override fun updateNotesByEventId(
        eventId: Long,
        notes: String?
    ) {
        queries.updateNotesByEventId(
            notes = notes,
            eventId = eventId
        )
    }

    override fun updateAgended(
        id: Long,
        agended: Int
    ) {
        queries.updateAgended(
            agended = agended.toLong(),
            id = id
        )
    }

    override fun getAll(): List<Collectible> {
        return queries.selectAll().executeAsList()
    }

    override fun getById(id: Long): Collectible? {   // ← CAMBIO: antes String
        return queries.selectById(id).executeAsOneOrNull()
    }

    override fun getByEventId(eventId: Long): Collectible? {
        return queries.selectByEventId(eventId).executeAsOneOrNull()
    }

    override fun deleteById(id: Long) {              // ← CAMBIO: antes String
        queries.deleteById(id)
    }

    override fun deleteByEventId(eventId: Long) {
        val existing = queries.selectByEventId(eventId).executeAsOneOrNull() ?: return
        queries.deleteById(existing.id)
    }
}
