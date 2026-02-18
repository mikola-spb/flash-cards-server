package com.khasanov.flashcards.cardset

import kotlinx.serialization.Serializable

@Serializable
data class CardSetResponse(
    val id: String,
    val name: String,
    val icon: String?,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class CreateCardSetRequest(
    val name: String,
    val icon: String? = null,
)

@Serializable
data class UpdateCardSetRequest(
    val name: String? = null,
    val icon: String? = null,
)
