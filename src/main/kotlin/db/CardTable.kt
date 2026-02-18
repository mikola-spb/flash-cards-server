package com.khasanov.flashcards.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object CardTable : Table("cards") {
    val id = uuid("id").autoGenerate()
    val cardSetId = uuid("card_set_id").references(CardSetTable.id)
    val frontText = text("front_text")
    val backText = text("back_text")
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")

    override val primaryKey = PrimaryKey(id)
}
