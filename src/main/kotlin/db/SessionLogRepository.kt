package com.khasanov.flashcards.db

import com.khasanov.flashcards.session.SaveSessionLogRecordsRequest
import com.khasanov.flashcards.session.SaveSessionLogRecordsResponse
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.OffsetDateTime
import java.util.*

class SessionLogRepository {

    suspend fun save(request: SaveSessionLogRecordsRequest): SaveSessionLogRecordsResponse = newSuspendedTransaction {
        val inserted = SessionLogTable.batchInsert(data = request.records, shouldReturnGeneratedValues = false) {
            this[SessionLogTable.id] = UUID.fromString(it.id)
            this[SessionLogTable.sessionId] = UUID.fromString(it.sessionId)
            this[SessionLogTable.cardId] = UUID.fromString(it.cardId)
            this[SessionLogTable.displayedAt] = OffsetDateTime.parse(it.displayedAt)
            this[SessionLogTable.flippedAt] = OffsetDateTime.parse(it.flippedAt)
            this[SessionLogTable.isKnown] = it.isKnown
        }
        SaveSessionLogRecordsResponse(inserted.size)
    }
}
