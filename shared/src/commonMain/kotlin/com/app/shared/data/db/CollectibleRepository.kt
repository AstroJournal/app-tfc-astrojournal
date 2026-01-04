package com.app.shared.data.db

import db.Collectible
import db.CollectibleQueries

class CollectibleRepository(
    private val queries: CollectibleQueries
) {

    fun insertCollectible(
        id: String,
        eventId: String,
        observationDate: String,
        notes: String?
    ) {
        queries.insertCollectible(
            id,
            eventId,
            observationDate,
            notes
        )
    }

    fun getAll(): List<Collectible> {
        return queries.selectAll().executeAsList()
    }

    fun getById(id: String): Collectible? {
        return queries.selectById(id).executeAsOneOrNull()
    }

    fun deleteById(id: String) {
        queries.deleteById(id)
    }

    fun deleteAll() {
        queries.deleteAll()
    }
}
