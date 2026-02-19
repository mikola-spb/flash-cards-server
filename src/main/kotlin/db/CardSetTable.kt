package com.khasanov.flashcards.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object CardSetTable : Table("card_sets") {
    val id = uuid("id").autoGenerate()
    val name = text("name")
    val icon = text("icon").nullable()
    val userId = uuid("user_id").references(UserTable.id)
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")

    override val primaryKey = PrimaryKey(id)
}
