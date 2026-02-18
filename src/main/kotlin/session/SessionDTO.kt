package com.khasanov.flashcards.session

import kotlinx.serialization.Serializable

@Serializable
data class SessionLogRecord(
    val id: String,
    val sessionId: String,
    val cardId: String,
    val displayedAt: String,
    val flippedAt: String,
    val isKnown: Boolean,
)

@Serializable
data class SaveSessionLogRecordsRequest(
    val records: List<SessionLogRecord>,
)

@Serializable
data class SaveSessionLogRecordsResponse(
    val count: Int,
)
