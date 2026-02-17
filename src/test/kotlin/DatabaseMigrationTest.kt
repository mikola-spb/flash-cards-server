package com.khasanov.flashcards

import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertTrue

class DatabaseMigrationTest {

    @Test
    fun `migration creates card_sets table`() {
        DatabaseTestSupport // triggers container + migration

        val tables = transaction {
            exec("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'") { rs ->
                buildList {
                    while (rs.next()) add(rs.getString("table_name"))
                }
            } ?: emptyList()
        }

        assertTrue("card_sets" in tables, "card_sets table should exist")
        assertTrue("cards" in tables, "cards table should exist")
    }
}
