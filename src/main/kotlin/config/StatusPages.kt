package com.khasanov.flashcards.config

import com.khasanov.flashcards.AuthorizationException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            if(cause is AuthorizationException) {
                this@configureStatusPages.log.warn("AuthorizationException occurred: ${cause.message}")
                call.respondText(text = "403: Forbidden" , status = HttpStatusCode.Forbidden)
            } else {
                this@configureStatusPages.log.error("Unexpected exception", cause)
                call.respondText(text = "500: Unexpected error" , status = HttpStatusCode.InternalServerError)
            }
        }
    }
}
