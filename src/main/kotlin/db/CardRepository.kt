package com.khasanov.flashcards.db

import com.khasanov.flashcards.card.CardResponse
import com.khasanov.flashcards.card.CreateCardRequest
import com.khasanov.flashcards.card.UpdateCardRequest
import io.ktor.util.logging.KtorSimpleLogger
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class CardRepository {
    private val logger = KtorSimpleLogger("com.khasanov.flashcards.db.CardRepository")

    suspend fun findAllByCardSetId(userId: UUID, cardSetId: UUID): List<CardResponse> = newSuspendedTransaction {
        CardTable.innerJoin(CardSetTable)
            .selectAll()
            .where { (CardTable.cardSetId eq cardSetId) and (CardSetTable.userId eq userId) }
            .map { it.toCardResponse() }
    }

    suspend fun findById(userId: UUID, cardSetId: UUID, cardId: UUID): CardResponse? = newSuspendedTransaction {
        CardTable.innerJoin(CardSetTable)
            .selectAll()
            .where { (CardTable.id eq cardId) and (CardTable.cardSetId eq cardSetId) and (CardSetTable.userId eq userId) }
            .singleOrNull()
            ?.toCardResponse()
    }

    suspend fun create(userId: UUID, cardSetId: UUID, request: CreateCardRequest): CardResponse? = newSuspendedTransaction {
        val cardSetExists = CardSetTable.selectAll()
            .where { (CardSetTable.id eq cardSetId) and (CardSetTable.userId eq userId) }
            .count() > 0
        if (!cardSetExists) {
            logger.info("User $userId tries to add a card into not existing or not owned card set $cardSetId")
            return@newSuspendedTransaction null
        }

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

    suspend fun update(userId: UUID, cardSetId: UUID, cardId: UUID, request: UpdateCardRequest): CardResponse? = newSuspendedTransaction {
        val ownsCardSet = CardSetTable.selectAll()
            .where { (CardSetTable.id eq cardSetId) and (CardSetTable.userId eq userId) }
            .count() > 0
        if (!ownsCardSet) {
            logger.info("User $userId tries to update a card of not existing or not owned card set $cardSetId")
            return@newSuspendedTransaction null
        }

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

    suspend fun delete(userId: UUID, cardSetId: UUID, cardId: UUID): Boolean = newSuspendedTransaction {
        val ownsCardSet = CardSetTable.selectAll()
            .where { (CardSetTable.id eq cardSetId) and (CardSetTable.userId eq userId) }
            .count() > 0
        if (!ownsCardSet) {
            logger.info("User $userId tries to delete a card from not existing or not owned card set $cardSetId")
            return@newSuspendedTransaction false
        }

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
