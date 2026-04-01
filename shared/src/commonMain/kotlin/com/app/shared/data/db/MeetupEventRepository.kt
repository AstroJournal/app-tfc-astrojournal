package com.app.shared.data.db

import com.astrojournal.shared.data.db.MeetupEvent
import com.astrojournal.shared.data.db.MeetupEventQueries

class MeetupEventRepository(
    private val queries: MeetupEventQueries
) {

    fun insertEvent(
        title: String,
        description: String,
        location: String,
        dateTime: String,
        isMine: Boolean,
        linkedAstroEventName: String? = null
    ) {
        queries.insertEvent(
            title = title,
            description = description,
            location = location,
            dateTime = dateTime,
            isMine = if (isMine) 1L else 0L,
            linkedAstroEventName = linkedAstroEventName
        )
    }

    fun updateEvent(
        id: Long,
        title: String,
        description: String,
        location: String,
        dateTime: String,
        linkedAstroEventName: String? = null
    ) {
        queries.updateEvent(
            title = title,
            description = description,
            location = location,
            dateTime = dateTime,
            linkedAstroEventName = linkedAstroEventName,
            id = id
        )
    }

    fun deleteById(id: Long) {
        queries.deleteById(id)
    }

    fun getAll(): List<MeetupEvent> {
        return queries.selectAll().executeAsList()
    }

    fun getMyEvents(): List<MeetupEvent> {
        return queries.selectMyEvents().executeAsList()
    }
}
