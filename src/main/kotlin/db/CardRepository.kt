package com.khasanov.flashcards.db

import com.khasanov.flashcards.card.CardResponse
import com.khasanov.flashcards.card.CreateCardRequest
import com.khasanov.flashcards.card.UpdateCardRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class CardRepository {

    suspend fun findAllByCardSetId(cardSetId: UUID): List<CardResponse> = newSuspendedTransaction {
        CardTable.selectAll()
            .where { CardTable.cardSetId eq cardSetId }
            .map { it.toCardResponse() }
    }

    suspend fun findById(cardSetId: UUID, cardId: UUID): CardResponse? = newSuspendedTransaction {
        CardTable.selectAll()
            .where { (CardTable.id eq cardId) and (CardTable.cardSetId eq cardSetId) }
            .singleOrNull()
            ?.toCardResponse()
    }

    suspend fun create(cardSetId: UUID, request: CreateCardRequest): CardResponse? = newSuspendedTransaction {
        val cardSetExists = CardSetTable.selectAll()
            .where { CardSetTable.id eq cardSetId }
            .count() > 0
        if (!cardSetExists) return@newSuspendedTransaction null

        val insertedId = CardTable.insert {
            it[CardTable.cardSetId] = cardSetId
            it[frontText] = request.frontText
            it[backText] = request.backText
        }[CardTable.id]

        CardTable.selectAll()
            .where { CardTable.id eq insertedId }
            .single()
            .toCardResponse()
    }

    suspend fun update(cardSetId: UUID, cardId: UUID, request: UpdateCardRequest): CardResponse? = newSuspendedTransaction {
        val updated = CardTable.update({ (CardTable.id eq cardId) and (CardTable.cardSetId eq cardSetId) }) {
            request.frontText?.let { value -> it[frontText] = value }
            request.backText?.let { value -> it[backText] = value }
            it[updatedAt] = CurrentTimestampWithTimeZone
        }
        if (updated == 0) return@newSuspendedTransaction null

        CardTable.selectAll()
            .where { CardTable.id eq cardId }
            .single()
            .toCardResponse()
    }

    suspend fun delete(cardSetId: UUID, cardId: UUID): Boolean = newSuspendedTransaction {
        CardTable.deleteWhere { (CardTable.id eq cardId) and (CardTable.cardSetId eq cardSetId) } > 0
    }

    private fun ResultRow.toCardResponse() = CardResponse(
        id = this[CardTable.id].toString(),
        cardSetId = this[CardTable.cardSetId].toString(),
        frontText = this[CardTable.frontText],
        backText = this[CardTable.backText],
        createdAt = this[CardTable.createdAt].toString(),
        updatedAt = this[CardTable.updatedAt].toString(),
    )
}
