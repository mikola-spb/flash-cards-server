package com.khasanov.flashcards.user

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

class UserRoutesTest : DatabaseTestSupport() {

    private fun ApplicationTestBuilder.configureApp() {
        application {
            configureSerialization()
            configureRouting()
        }
    }

    @Test
    fun `POST creates a new user`() = testApplication {
        configureApp()

        val response = client.post("/api/users") {
            header("X-User-Id", "new-external-id")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val body = Json.decodeFromString<UserResponse>(response.bodyAsText())
        assertEquals("new-external-id", body.externalId)
        assertNotNull(body.id)
        assertNotNull(body.createdAt)
    }

    @Test
    fun `POST returns 409 if user already exists`() = testApplication {
        configureApp()

        val response = client.post("/api/users") {
            header("X-User-Id", "test-user-external-id")
        }

        assertEquals(HttpStatusCode.Conflict, response.status)
        val body = Json.decodeFromString<UserResponse>(response.bodyAsText())
        assertEquals("test-user-external-id", body.externalId)
    }

    @Test
    fun `POST returns 400 without X-User-Id header`() = testApplication {
        configureApp()

        val response = client.post("/api/users")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
