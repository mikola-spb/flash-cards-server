package com.khasanov.flashcards.config

import com.khasanov.flashcards.card.cardRoutes
import com.khasanov.flashcards.cardset.cardSetRoutes
import com.khasanov.flashcards.session.sessionRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("OK")
        }
        cardSetRoutes()
        cardRoutes()
        sessionRoutes()
    }
}
