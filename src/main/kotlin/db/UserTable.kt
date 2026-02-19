package com.khasanov.flashcards.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object UserTable : Table("users") {
    val id = uuid("id").autoGenerate()
    val externalId = text("external_id")
    val createdAt = timestampWithTimeZone("created_at")

    override val primaryKey = PrimaryKey(id)
}
