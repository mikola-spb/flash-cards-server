package com.khasanov.flashcards.config

import io.ktor.server.application.*
import io.ktor.util.*
import java.util.*

private val UserIdKey = AttributeKey<UUID>("userId")

fun ApplicationCall.setUserId(userId: UUID) {
    attributes.put(UserIdKey, userId)
}

fun ApplicationCall.userId(): UUID = attributes[UserIdKey]
