package com.khasanov.flashcards.user

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val externalId: String,
    val createdAt: String,
)
