package com.khasanov.flashcards.card

import com.khasanov.flashcards.DatabaseTestSupport
import com.khasanov.flashcards.cardset.CardSetResponse
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

class CardRoutesTest : DatabaseTestSupport() {

    private fun ApplicationTestBuilder.configureApp() {
        application {
            configureSerialization()
            configureRouting()
        }
    }

    private suspend fun ApplicationTestBuilder.createCardSet(name: String = "German basics"): CardSetResponse {
        val response = client.post("/api/card-sets") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"$name"}""")
        }
        return Json.decodeFromString(response.bodyAsText())
    }

    @Test
    fun `POST creates a card`() = testApplication {
        configureApp()
        val cardSet = createCardSet()

        val response = client.post("/api/card-sets/${cardSet.id}/cards") {
            contentType(ContentType.Application.Json)
            setBody("""{"frontText":"hello","backText":"Hallo"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val body = Json.decodeFromString<CardResponse>(response.bodyAsText())
        assertEquals("hello", body.frontText)
        assertEquals("Hallo", body.backText)
        assertEquals(cardSet.id, body.cardSetId)
        assertNotNull(body.id)
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
    fun `GET returns all cards for a card set`() = testApplication {
        configureApp()
        val cardSet = createCardSet()

        client.post("/api/card-sets/${cardSet.id}/cards") {
            contentType(ContentType.Application.Json)
            setBody("""{"frontText":"hello","backText":"Hallo"}""")
        }
        client.post("/api/card-sets/${cardSet.id}/cards") {
            contentType(ContentType.Application.Json)
            setBody("""{"frontText":"goodbye","backText":"Auf Wiedersehen"}""")
        }

        val response = client.get("/api/card-sets/${cardSet.id}/cards")
        assertEquals(HttpStatusCode.OK, response.status)
        val cards = Json.decodeFromString<List<CardResponse>>(response.bodyAsText())
        assertEquals(2, cards.size)
    }

    @Test
    fun `GET by id returns a card`() = testApplication {
        configureApp()
        val cardSet = createCardSet()

        val createResponse = client.post("/api/card-sets/${cardSet.id}/cards") {
            contentType(ContentType.Application.Json)
            setBody("""{"frontText":"hello","backText":"Hallo"}""")
        }
        val created = Json.decodeFromString<CardResponse>(createResponse.bodyAsText())

        val response = client.get("/api/card-sets/${cardSet.id}/cards/${created.id}")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.decodeFromString<CardResponse>(response.bodyAsText())
        assertEquals("hello", body.frontText)
    }

    @Test
    fun `GET by id returns 404 for unknown card`() = testApplication {
        configureApp()
        val cardSet = createCardSet()

        val response = client.get("/api/card-sets/${cardSet.id}/cards/00000000-0000-0000-0000-000000000000")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `PUT updates a card`() = testApplication {
        configureApp()
        val cardSet = createCardSet()

        val createResponse = client.post("/api/card-sets/${cardSet.id}/cards") {
            contentType(ContentType.Application.Json)
            setBody("""{"frontText":"old","backText":"alt"}""")
        }
        val created = Json.decodeFromString<CardResponse>(createResponse.bodyAsText())

        val response = client.put("/api/card-sets/${cardSet.id}/cards/${created.id}") {
            contentType(ContentType.Application.Json)
            setBody("""{"frontText":"new","backText":"neu"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.decodeFromString<CardResponse>(response.bodyAsText())
        assertEquals("new", body.frontText)
        assertEquals("neu", body.backText)
    }

    @Test
    fun `DELETE removes a card`() = testApplication {
        configureApp()
        val cardSet = createCardSet()

        val createResponse = client.post("/api/card-sets/${cardSet.id}/cards") {
            contentType(ContentType.Application.Json)
            setBody("""{"frontText":"to be deleted","backText":"wird gelöscht"}""")
        }
        val created = Json.decodeFromString<CardResponse>(createResponse.bodyAsText())

        val deleteResponse = client.delete("/api/card-sets/${cardSet.id}/cards/${created.id}")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        val getResponse = client.get("/api/card-sets/${cardSet.id}/cards/${created.id}")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)
    }

    @Test
    fun `DELETE returns 404 for unknown card`() = testApplication {
        configureApp()
        val cardSet = createCardSet()

        val response = client.delete("/api/card-sets/${cardSet.id}/cards/00000000-0000-0000-0000-000000000000")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
