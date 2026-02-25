package com.app.astrojournal.data.model

/**
 * Representa un evento astronómico (fase lunar, conjunción, etc.)
 */
data class AstroEvent(
    val name: String,
    val description: String,
    val date: String,
    val timestamp: Long, // Añadido para permitir ordenación cronológica precisa
    val type: EventType
)

/**
 * Diferentes tipos de eventos para mostrar iconos o estilos distintos
 */
enum class EventType {
    MOON_PHASE,
    CONJUNCTION,
    PLANET,
    OTHER
}
