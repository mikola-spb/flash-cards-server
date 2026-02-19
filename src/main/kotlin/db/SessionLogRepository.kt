package com.khasanov.flashcards.db

import com.khasanov.flashcards.AuthorizationException
import com.khasanov.flashcards.session.SaveSessionLogRecordsRequest
import com.khasanov.flashcards.session.SaveSessionLogRecordsResponse
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.OffsetDateTime
import java.util.*

class SessionLogRepository {

    suspend fun save(userId: UUID, request: SaveSessionLogRecordsRequest): SaveSessionLogRecordsResponse = newSuspendedTransaction {
        // Verify all referenced cards belong to the user
        val cardIds = request.records.map { UUID.fromString(it.cardId) }.toSet()
        val unauthorizedCardsCount = CardTable.innerJoin(CardSetTable)
            .select(CardTable.id)
            .where { (CardSetTable.userId neq userId) and (CardTable.id inList cardIds) }
            .count()

        if (unauthorizedCardsCount > 0) {
            throw AuthorizationException("Unauthorized session log records submission from user: $userId.")
        }

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
