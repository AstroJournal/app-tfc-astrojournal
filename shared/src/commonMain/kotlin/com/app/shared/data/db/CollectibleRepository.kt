package com.app.shared.data.db

import com.astrojournal.shared.data.db.Collectible
import com.astrojournal.shared.data.db.CollectibleQueries

class CollectibleRepository(
    private val queries: CollectibleQueries
) {

    fun insertCollectible(
        eventId: Long,                 // ← CAMBIO: antes String
        observationDate: String,
        notes: String?,
        observed: Int
    ) {
        queries.insertCollectible(
            eventId = eventId,
            observationDate = observationDate,
            notes = notes,
            observed = observed.toLong()
        )
    }

    fun updateObserved(
        id: Long,                      // ← CAMBIO: antes String
        observed: Int
    ) {
        queries.updateObserved(
            observed = observed.toLong(),
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
