package com.khasanov.flashcards.config

import com.khasanov.flashcards.card.cardRoutes
import com.khasanov.flashcards.cardset.cardSetRoutes
import com.khasanov.flashcards.session.sessionRoutes
import com.khasanov.flashcards.statistic.statisticRoutes
import com.khasanov.flashcards.user.userRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("OK")
        }
        userRoutes()
        cardSetRoutes()
        cardRoutes()
        sessionRoutes()
        statisticRoutes()
    }
}
