package com.khasanov.flashcards.config

import com.khasanov.flashcards.db.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import java.util.*

val UserInterceptor = createRouteScopedPlugin("UserInterceptor") {
    val userRepository = UserRepository()

    onCall { call ->
        if (call.request.local.method == HttpMethod.Post &&
            call.request.local.uri.startsWith("/api/users")
        ) {
            return@onCall
        }

        val externalId = call.request.headers["X-User-Id"]
        if (externalId == null) {
            call.respond(HttpStatusCode.Unauthorized, "Missing X-User-Id header")
            return@onCall
        }

        val user = userRepository.findByExternalId(externalId)
        if (user == null) {
            call.respond(HttpStatusCode.Unauthorized, "User not found")
            return@onCall
        }

        call.setUserId(UUID.fromString(user.id))
    }
}
