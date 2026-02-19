package com.khasanov.flashcards

import com.khasanov.flashcards.config.configureDatabase
import com.khasanov.flashcards.config.configureRouting
import com.khasanov.flashcards.config.configureSerialization
import com.khasanov.flashcards.config.configureStatusPages
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDatabase()
    configureSerialization()
    configureRouting()
    configureStatusPages()
}
