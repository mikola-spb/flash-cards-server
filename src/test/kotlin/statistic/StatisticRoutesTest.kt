package com.khasanov.flashcards.statistic

import com.khasanov.flashcards.DatabaseTestSupport
import com.khasanov.flashcards.config.configureRouting
import com.khasanov.flashcards.config.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

private const val CARD_ID = "22222222-2222-2222-2222-222222222222"

class StatisticRoutesTest : DatabaseTestSupport() {

    private fun ApplicationTestBuilder.configureApp() {
        application {
            configureSerialization()
            configureRouting()
        }
    }

    @Test
    fun `GET returns statistic for all cards`() = testApplication {
        configureApp()

        val response = client.get("/api/statistic")
        assertEquals(HttpStatusCode.OK, response.status)
        val statistic = Json.decodeFromString<StatisticResponse>(response.bodyAsText())
        assertEquals(1, statistic.cardStatistics.size)
        assertContains(statistic.cardStatistics, CardStatistic(
            cardId = CARD_ID,
            totalSessions = 2,
            totalAttempts = 3,
            successfulAttempts = 2,
            successStreak = 1,
            lastSeenAt = "2026-02-18T15:00Z",
            state = CardState.LEARNING
        ))
    }
}
