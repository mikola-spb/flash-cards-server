package com.khasanov.flashcards.card

import com.khasanov.flashcards.db.CardRepository
import com.khasanov.flashcards.toUUIDOrNull
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.cardRoutes(repository: CardRepository = CardRepository()) {
    route("/api/card-sets/{cardSetId}/cards") {
        get {
            val cardSetId = call.parameters["cardSetId"]?.toUUIDOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid cardSetId")

            call.respond(repository.findAllByCardSetId(cardSetId))
        }

        get("/{cardId}") {
            val cardSetId = call.parameters["cardSetId"]?.toUUIDOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid cardSetId")
            val cardId = call.parameters["cardId"]?.toUUIDOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid cardId")

            val card = repository.findById(cardSetId, cardId)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            call.respond(card)
        }

        post {
            val cardSetId = call.parameters["cardSetId"]?.toUUIDOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid cardSetId")

            val request = call.receive<CreateCardRequest>()
            val created = repository.create(cardSetId, request)
                ?: return@post call.respond(HttpStatusCode.NotFound, "Card set not found")

            call.respond(HttpStatusCode.Created, created)
        }

        put("/{cardId}") {
            val cardSetId = call.parameters["cardSetId"]?.toUUIDOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid cardSetId")
            val cardId = call.parameters["cardId"]?.toUUIDOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid cardId")

            val request = call.receive<UpdateCardRequest>()
            val updated = repository.update(cardSetId, cardId, request)
                ?: return@put call.respond(HttpStatusCode.NotFound)

            call.respond(updated)
        }

        delete("/{cardId}") {
            val cardSetId = call.parameters["cardSetId"]?.toUUIDOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid cardSetId")
            val cardId = call.parameters["cardId"]?.toUUIDOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid cardId")

            if (repository.delete(cardSetId, cardId)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
