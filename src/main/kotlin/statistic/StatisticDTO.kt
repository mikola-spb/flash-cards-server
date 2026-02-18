package com.khasanov.flashcards.statistic

import kotlinx.serialization.Serializable

enum class CardState {
    NEW, HARD, LEARNING, FAMILIAR, KNOWN
}

@Serializable
data class CardStatistic(
    val cardId: String,
    val totalSessions: Int,
    val totalAttempts: Int,
    val successfulAttempts: Int,
    val successStreak: Int,
    val lastSeenAt: String,
    val state: CardState,
)

@Serializable
data class StatisticResponse(
    val cardStatistics: List<CardStatistic>,
)
