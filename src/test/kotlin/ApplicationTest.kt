package com.khasanov.flashcards

import com.khasanov.flashcards.config.configureRouting
import com.khasanov.flashcards.config.configureSerialization
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            configureSerialization()
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

}
