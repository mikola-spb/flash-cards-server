package com.khasanov.flashcards.card

import kotlinx.serialization.Serializable

@Serializable
data class CardResponse(
    val id: String,
    val cardSetId: String,
    val frontText: String,
    val backText: String,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class CreateCardRequest(
    val frontText: String,
    val backText: String,
)

@Serializable
data class UpdateCardRequest(
    val frontText: String? = null,
    val backText: String? = null,
)
