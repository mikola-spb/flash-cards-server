package com.khasanov.flashcards.db

import com.khasanov.flashcards.cardset.CardSetResponse
import com.khasanov.flashcards.cardset.CreateCardSetRequest
import com.khasanov.flashcards.cardset.UpdateCardSetRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class CardSetRepository {

    suspend fun findAll(userId: UUID): List<CardSetResponse> = newSuspendedTransaction {
        CardSetTable.selectAll()
            .where { CardSetTable.userId eq userId }
            .map { it.toCardSetResponse() }
    }

    suspend fun findById(userId: UUID, id: UUID): CardSetResponse? = newSuspendedTransaction {
        CardSetTable.selectAll()
            .where { (CardSetTable.id eq id) and (CardSetTable.userId eq userId) }
            .singleOrNull()
            ?.toCardSetResponse()
    }

    suspend fun create(userId: UUID, request: CreateCardSetRequest): CardSetResponse = newSuspendedTransaction {
        val insertedId = CardSetTable.insert {
            it[name] = request.name
            it[icon] = request.icon
            it[CardSetTable.userId] = userId
        }[CardSetTable.id]

        CardSetTable.selectAll()
            .where { CardSetTable.id eq insertedId }
            .single()
            .toCardSetResponse()
    }

    suspend fun update(userId: UUID, id: UUID, request: UpdateCardSetRequest): CardSetResponse? = newSuspendedTransaction {
        val updated = CardSetTable.update({ (CardSetTable.id eq id) and (CardSetTable.userId eq userId) }) {
            request.name?.let { value -> it[name] = value }
            request.icon?.let { value -> it[icon] = value }
            it[updatedAt] = CurrentTimestampWithTimeZone
        }
        if (updated == 0) return@newSuspendedTransaction null

        CardSetTable.selectAll()
            .where { (CardSetTable.id eq id) and (CardSetTable.userId eq userId) }
            .single()
            .toCardSetResponse()
    }

    suspend fun delete(userId: UUID, id: UUID): Boolean = newSuspendedTransaction {
        CardSetTable.deleteWhere { (CardSetTable.id eq id) and (CardSetTable.userId eq userId) } > 0
    }

    private fun ResultRow.toCardSetResponse() = CardSetResponse(
        id = this[CardSetTable.id].toString(),
        name = this[CardSetTable.name],
        icon = this[CardSetTable.icon],
        createdAt = this[CardSetTable.createdAt].toString(),
        updatedAt = this[CardSetTable.updatedAt].toString(),
    )
}
