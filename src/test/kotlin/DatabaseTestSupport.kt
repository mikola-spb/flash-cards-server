package com.khasanov.flashcards

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.testcontainers.containers.PostgreSQLContainer

object DatabaseTestSupport {

    private val postgres = PostgreSQLContainer("postgres:17").apply {
        withDatabaseName("flashcards_test")
        withUsername("test")
        withPassword("test")
        start()
    }

    val dataSource: HikariDataSource = HikariDataSource(HikariConfig().apply {
        jdbcUrl = postgres.jdbcUrl
        username = postgres.username
        password = postgres.password
        maximumPoolSize = 5
        isAutoCommit = false
    })

    init {
        Flyway.configure()
            .dataSource(dataSource)
            .load()
            .migrate()

        Database.connect(dataSource)
    }
}
