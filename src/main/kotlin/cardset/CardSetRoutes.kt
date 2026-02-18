package com.khasanov.flashcards.cardset

import com.khasanov.flashcards.db.CardSetRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.cardSetRoutes(repository: CardSetRepository = CardSetRepository()) {
    route("/api/card-sets") {
        get {
            call.respond(repository.findAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toUUID()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid id")

            val cardSet = repository.findById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            call.respond(cardSet)
        }

        post {
            val request = call.receive<CreateCardSetRequest>()
            val created = repository.create(request)
            call.respond(HttpStatusCode.Created, created)
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toUUID()
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid id")

            val request = call.receive<UpdateCardSetRequest>()
            val updated = repository.update(id, request)
                ?: return@put call.respond(HttpStatusCode.NotFound)

            call.respond(updated)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toUUID()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid id")

            if (repository.delete(id)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

private fun String.toUUID(): UUID? = try {
    UUID.fromString(this)
} catch (_: IllegalArgumentException) {
    null
}
