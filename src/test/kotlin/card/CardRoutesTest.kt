package com.khasanov.flashcards.card

import com.khasanov.flashcards.DatabaseTestSupport
import com.khasanov.flashcards.config.configureRouting
import com.khasanov.flashcards.config.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

private const val CARD_SET_ID = "11111111-1111-1111-1111-111111111111"
private const val CARD_ID = "22222222-2222-2222-2222-222222222222"

class CardRoutesTest : DatabaseTestSupport() {

    private fun ApplicationTestBuilder.configureApp() {
        application {
            configureSerialization()
            configureRouting()
        }
    }

    @Test
    fun `GET returns all cards for a card set`() = testApplication {
        configureApp()

        val response = client.get("/api/card-sets/${CARD_SET_ID}/cards")
        assertEquals(HttpStatusCode.OK, response.status)
        val cards = Json.decodeFromString<List<CardResponse>>(response.bodyAsText())
        assertEquals(1, cards.size)
        assertEquals("hello", cards[0].frontText)
        assertEquals("Hallo", cards[0].backText)
    }

    @Test
    fun `GET by id returns a card`() = testApplication {
        configureApp()

        val response = client.get("/api/card-sets/${CARD_SET_ID}/cards/$CARD_ID")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.decodeFromString<CardResponse>(response.bodyAsText())
        assertEquals("hello", body.frontText)
        assertEquals("Hallo", body.backText)
        assertEquals(CARD_SET_ID, body.cardSetId)
    }

    @Test
    fun `GET by id returns 404 for unknown card`() = testApplication {
        configureApp()

        val response = client.get("/api/card-sets/${CARD_SET_ID}/cards/00000000-0000-0000-0000-000000000000")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `POST creates a card`() = testApplication {
        configureApp()

        val response = client.post("/api/card-sets/${CARD_SET_ID}/cards") {
            contentType(ContentType.Application.Json)
            setBody("""{"frontText":"goodbye","backText":"Auf Wiedersehen"}""")
        }

        // created card
        assertEquals(HttpStatusCode.Created, response.status)
        val body = Json.decodeFromString<CardResponse>(response.bodyAsText())
        assertEquals("goodbye", body.frontText)
        assertEquals("Auf Wiedersehen", body.backText)
        assertEquals(CARD_SET_ID, body.cardSetId)
        assertNotNull(body.id)

        // all cards in the set
        val responseAllCards = client.get("/api/card-sets/${CARD_SET_ID}/cards")
        assertEquals(HttpStatusCode.OK, responseAllCards.status)
        val allCards = Json.decodeFromString<List<CardResponse>>(responseAllCards.bodyAsText())
        assertEquals(2, allCards.size)
    }

    @Test
    fun `POST returns 404 for non-existent card set`() = testApplication {
        configureApp()

        val response = client.post("/api/card-sets/00000000-0000-0000-0000-000000000000/cards") {
            contentType(ContentType.Application.Json)
            setBody("""{"frontText":"hello","backText":"Hallo"}""")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `PUT updates a card`() = testApplication {
        configureApp()

        val response = client.put("/api/card-sets/${CARD_SET_ID}/cards/$CARD_ID") {
            contentType(ContentType.Application.Json)
            setBody("""{"frontText":"hi","backText":"Hallo!"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.decodeFromString<CardResponse>(response.bodyAsText())
        assertEquals("hi", body.frontText)
        assertEquals("Hallo!", body.backText)

        // all cards in the set
        val responseAllCards = client.get("/api/card-sets/${CARD_SET_ID}/cards")
        assertEquals(HttpStatusCode.OK, responseAllCards.status)
        val allCards = Json.decodeFromString<List<CardResponse>>(responseAllCards.bodyAsText())
        assertEquals(1, allCards.size)
    }

    @Test
    fun `DELETE removes a card`() = testApplication {
        configureApp()

        // Create a throwaway card for deletion
        val createResponse = client.post("/api/card-sets/${CARD_SET_ID}/cards") {
            contentType(ContentType.Application.Json)
            setBody("""{"frontText":"to delete","backText":"löschen"}""")
        }
        val created = Json.decodeFromString<CardResponse>(createResponse.bodyAsText())

        val deleteResponse = client.delete("/api/card-sets/${CARD_SET_ID}/cards/${created.id}")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        val getResponse = client.get("/api/card-sets/${CARD_SET_ID}/cards/${created.id}")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)
    }

    @Test
    fun `DELETE returns 404 for unknown card`() = testApplication {
        configureApp()

        val response = client.delete("/api/card-sets/${CARD_SET_ID}/cards/00000000-0000-0000-0000-000000000000")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
