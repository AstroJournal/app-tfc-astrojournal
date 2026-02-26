package com.app.shared.data.db

import com.astrojournal.shared.data.db.Collectible
import com.astrojournal.shared.data.db.CollectibleQueries

class CollectibleRepository(
    private val queries: CollectibleQueries
) {

    fun insertCollectible(
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

    fun updateObserved(
        id: Long,
        observed: Int
    ) {
        queries.updateObserved(
            observed = observed.toLong(),
            id = id
        )
    }

    fun updateNotes(
        id: Long,
        notes: String?
    ) {
        queries.updateNotes(
            notes = notes,
            id = id
        )
    }

    fun updateNotesByEventId(
        eventId: Long,
        notes: String?
    ) {
        queries.updateNotesByEventId(
            notes = notes,
            eventId = eventId
        )
    }

    fun updateAgended(
        id: Long,
        agended: Int
    ) {
        queries.updateAgended(
            agended = agended.toLong(),
            id = id
        )
    }

    fun getAll(): List<Collectible> {
        return queries.selectAll().executeAsList()
    }

    fun getById(id: Long): Collectible? {   // ← CAMBIO: antes String
        return queries.selectById(id).executeAsOneOrNull()
    }

    fun deleteById(id: Long) {              // ← CAMBIO: antes String
        queries.deleteById(id)
    }
}
