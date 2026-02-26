package com.app.astrojournal.data.model

import com.app.astrojournal.R

object EventData {
    val events = listOf(
        Event(
            id = 1,
            name = "Mars Opposition",
            dateTime = "June 15, 2024",
            planetImageRes = 0
        ),
        Event(
            id = 2,
            name = "Jupiter at Opposition",
            dateTime = "December 7, 2024",
            planetImageRes = 0
        ),
        Event(
            id = 3,
            name = "Saturn Rings Visible",
            dateTime = "September 8, 2024",
            planetImageRes = 0
        )
    )
    
    fun getEventById(id: Long): Event? {
        return events.find { it.id == id }
    }
}