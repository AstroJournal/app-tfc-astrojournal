package com.app.shared.data.db

import com.astrojournal.shared.data.db.Collectible

interface CollectibleStore {
    fun insertCollectible(
        eventId: Long,
        eventName: String,
        observationDate: String,
        notes: String?,
        observed: Int,
        agended: Int
    )

    fun updateObserved(id: Long, observed: Int)

    fun updateNotes(id: Long, notes: String?)

    fun updateNotesByEventId(eventId: Long, notes: String?)

    fun updateAgended(id: Long, agended: Int)

    fun getAll(): List<Collectible>

    fun getById(id: Long): Collectible?

    fun getByEventId(eventId: Long): Collectible?

    fun deleteById(id: Long)

    fun deleteByEventId(eventId: Long)
}
