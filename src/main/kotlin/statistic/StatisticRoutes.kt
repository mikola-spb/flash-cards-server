package com.khasanov.flashcards.statistic

import com.khasanov.flashcards.db.StatisticRepository
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.statisticRoutes(repository: StatisticRepository = StatisticRepository()) {
    route("/api/statistic") {
        get {
            call.respond(repository.findAll())
        }
    }
}
