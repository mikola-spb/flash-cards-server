package com.khasanov.flashcards.user

import com.khasanov.flashcards.db.UserRepository
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(repository: UserRepository = UserRepository()) {
    route("/users") {
        post {
            val externalId = call.request.headers["X-User-Id"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing X-User-Id header")

            val existing = repository.findByExternalId(externalId)
            if (existing != null) {
                return@post call.respond(HttpStatusCode.Conflict, existing)
            }

            val created = repository.create(externalId)
            call.respond(HttpStatusCode.Created, created)
        }
    }
}
