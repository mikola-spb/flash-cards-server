package com.khasanov.flashcards.db

import com.khasanov.flashcards.statistic.CardState
import com.khasanov.flashcards.statistic.CardStatistic
import com.khasanov.flashcards.statistic.StatisticResponse
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class StatisticRepository {

    suspend fun findAll(userId: UUID): StatisticResponse = newSuspendedTransaction {
        val sessionLog = SessionLogTable
            .innerJoin(CardTable)
            .innerJoin(CardSetTable)
            .select(SessionLogTable.columns)
            .where { CardSetTable.userId eq userId }
            .orderBy(
                SessionLogTable.cardId to SortOrder.ASC,
                SessionLogTable.displayedAt to SortOrder.DESC,
            )
        val cardStatistics = sessionLog.groupBy { it[SessionLogTable.cardId] }
            .map { entry -> entry.value.toCardStatistic(entry.key) }

        StatisticResponse(
            cardStatistics = cardStatistics
        )
    }

    private fun List<ResultRow>.toCardStatistic(uuid: UUID): CardStatistic {
        val successStreak = this.countSuccessStreak()
        return CardStatistic(
            cardId = uuid.toString(),
            totalSessions = this.distinctBy { it[SessionLogTable.sessionId] }.size,
            totalAttempts = this.size,
            successfulAttempts = this.filter { it[SessionLogTable.isKnown] }.size,
            successStreak = successStreak,
            lastSeenAt = this.first()[SessionLogTable.displayedAt].toString(),
            state = calculateState(this.size, successStreak),
        )
    }

    private fun calculateState(totalAttempts: Int, successStreak: Int): CardState {
        if (totalAttempts == 0) return CardState.NEW

        if (successStreak > 3) return CardState.KNOWN
        if (successStreak > 1) return CardState.FAMILIAR

        return CardState.LEARNING
    }

    private fun List<ResultRow>.countSuccessStreak(): Int {
        val firstFailureIdx = this.indexOfFirst { !it[SessionLogTable.isKnown] }
        if (firstFailureIdx == 0) return 0

        // only success answers or nothing
        if (firstFailureIdx == -1) return this.size

        // exclude success answer from the same session where the last wrong answer was given
        // count only direct success answer
        if (this[firstFailureIdx - 1][SessionLogTable.sessionId] == this[firstFailureIdx][SessionLogTable.sessionId]) {
            return firstFailureIdx - 1
        }
        return firstFailureIdx
    }
}
