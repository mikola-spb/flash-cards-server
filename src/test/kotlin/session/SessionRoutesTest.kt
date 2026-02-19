package com.khasanov.flashcards.session

import com.khasanov.flashcards.DatabaseTestSupport
import com.khasanov.flashcards.TEST_USER_EXTERNAL_ID
import com.khasanov.flashcards.config.configureRouting
import com.khasanov.flashcards.config.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

private const val CARD_ID = "22222222-2222-2222-2222-222222222222"

class SessionRoutesTest : DatabaseTestSupport() {

    private fun ApplicationTestBuilder.configureApp() {
        application {
            configureSerialization()
            configureRouting()
        }
    }

    @Test
    fun `POST saves all log records`() = testApplication {
        configureApp()

        val sessionId = "11111111-0000-0000-1111-000000000000"
        val response = client.post("/api/sessions") {
            header("X-User-Id", TEST_USER_EXTERNAL_ID)
            contentType(ContentType.Application.Json)
            setBody("""{"records":[
                {"id":"11111111-0000-0000-1111-000000000001","sessionId":"$sessionId","cardId":"$CARD_ID","displayedAt":"2026-02-18T14:30:00.000Z","flippedAt":"2026-02-18T14:30:02.000Z","isKnown":false},
                {"id":"11111111-0000-0000-1111-000000000002","sessionId":"$sessionId","cardId":"$CARD_ID","displayedAt":"2026-02-18T14:30:03.000Z","flippedAt":"2026-02-18T14:30:04.000Z","isKnown":true}
            ]}""".trimIndent())
        }

        // created card
        assertEquals(HttpStatusCode.Created, response.status)
        val body = Json.decodeFromString<SaveSessionLogRecordsResponse>(response.bodyAsText())
        assertEquals(2, body.count)
    }
}
