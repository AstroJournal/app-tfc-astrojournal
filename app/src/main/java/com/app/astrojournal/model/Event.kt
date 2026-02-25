package com.app.astrojournal.model

data class Event(
    val id: Long,
    val name: String,
    val dateTime: String,
    val planetImageRes: Int,
    val observed: Boolean = false
)