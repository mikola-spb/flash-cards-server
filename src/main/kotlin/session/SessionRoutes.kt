package com.khasanov.flashcards.session

import com.khasanov.flashcards.db.SessionLogRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.sessionRoutes(repository: SessionLogRepository = SessionLogRepository()) {
    route("/sessions") {
        post {
            val request = call.receive<SaveSessionLogRecordsRequest>()
            val response = repository.save(request)
            call.respond(HttpStatusCode.Created, response)
        }
    }
}
