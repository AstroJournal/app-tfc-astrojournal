package com.app.astrojournal.model

import com.app.astrojournal.R

object EventData {
    val events = listOf(
        Event(
            id = 1L,
            name = "Luna llena",
            dateTime = "2026-01-05 22:00",
            planetImageRes = R.drawable.moon,
            observed = false
        ),
        Event(
            id = 2L,
            name = "Eclipse solar",
            dateTime = "2026-02-12 10:00",
            planetImageRes = R.drawable.eclipse,
            observed = false
        )
    )
}
