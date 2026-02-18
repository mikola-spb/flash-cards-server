package com.khasanov.flashcards.cardset

import com.khasanov.flashcards.DatabaseTestSupport
import com.khasanov.flashcards.config.configureRouting
import com.khasanov.flashcards.config.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CardSetRoutesTest: DatabaseTestSupport() {

    private fun ApplicationTestBuilder.configureApp() {
        application {
            configureSerialization()
            configureRouting()
        }
    }

    @Test
    fun `POST creates a card set`() = testApplication {
        configureApp()

        val response = client.post("/api/card-sets") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Spanish","icon":"flag"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val body = Json.decodeFromString<CardSetResponse>(response.bodyAsText())
        assertEquals("Spanish", body.name)
        assertEquals("flag", body.icon)
        assertNotNull(body.id)
    }

    @Test
    fun `GET returns all card sets`() = testApplication {
        configureApp()

        client.post("/api/card-sets") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Spanish"}""")
        }
        client.post("/api/card-sets") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"French"}""")
        }

        val response = client.get("/api/card-sets")
        assertEquals(HttpStatusCode.OK, response.status)
        val sets = Json.decodeFromString<List<CardSetResponse>>(response.bodyAsText())
        assertEquals(2, sets.size)
    }

    @Test
    fun `GET by id returns a card set`() = testApplication {
        configureApp()

        val createResponse = client.post("/api/card-sets") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"German"}""")
        }
        val created = Json.decodeFromString<CardSetResponse>(createResponse.bodyAsText())

        val response = client.get("/api/card-sets/${created.id}")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.decodeFromString<CardSetResponse>(response.bodyAsText())
        assertEquals("German", body.name)
    }

    @Test
    fun `GET by id returns 404 for unknown id`() = testApplication {
        configureApp()

        val response = client.get("/api/card-sets/00000000-0000-0000-0000-000000000000")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `PUT updates a card set`() = testApplication {
        configureApp()

        val createResponse = client.post("/api/card-sets") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Old Name"}""")
        }
        val created = Json.decodeFromString<CardSetResponse>(createResponse.bodyAsText())

        val response = client.put("/api/card-sets/${created.id}") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"New Name","icon":"star"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.decodeFromString<CardSetResponse>(response.bodyAsText())
        assertEquals("New Name", body.name)
        assertEquals("star", body.icon)
    }

    @Test
    fun `DELETE removes a card set`() = testApplication {
        configureApp()

        val createResponse = client.post("/api/card-sets") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"To be deleted"}""")
        }
        val created = Json.decodeFromString<CardSetResponse>(createResponse.bodyAsText())

        val deleteResponse = client.delete("/api/card-sets/${created.id}")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        val getResponse = client.get("/api/card-sets/${created.id}")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)
    }

    @Test
    fun `DELETE returns 404 for unknown id`() = testApplication {
        configureApp()

        val response = client.delete("/api/card-sets/00000000-0000-0000-0000-000000000000")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
