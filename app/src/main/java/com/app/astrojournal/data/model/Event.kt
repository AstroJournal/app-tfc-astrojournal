package com.app.astrojournal.data.model

data class Event(
    val id: Long,
    val name: String,
    val dateTime: String,
    val planetImageRes: Int
)