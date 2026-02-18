package com.khasanov.flashcards.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object SessionLogTable : Table("session_log") {
    val id = uuid("id")
    val sessionId = uuid("session_id")
    val cardId = uuid("card_id").references(CardTable.id)
    val displayedAt = timestampWithTimeZone("displayed_at")
    val flippedAt = timestampWithTimeZone("flipped_at")
    val isKnown = bool("is_known")

    override val primaryKey = PrimaryKey(id)
}
