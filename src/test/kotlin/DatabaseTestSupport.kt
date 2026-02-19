package com.khasanov.flashcards

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.test.BeforeTest

const val TEST_USER_EXTERNAL_ID = "test-user-external-id"

open class DatabaseTestSupport {

    private val postgres = PostgreSQLContainer("postgres:17").apply {
        withDatabaseName("flashcards_test")
        withUsername("test")
        withPassword("test")
        start()
    }

    private val dataSource: HikariDataSource = HikariDataSource(HikariConfig().apply {
        jdbcUrl = postgres.jdbcUrl
        username = postgres.username
        password = postgres.password
        maximumPoolSize = 5
        isAutoCommit = false
    })

    private val flyway = Flyway.configure()
        .dataSource(dataSource)
        .cleanDisabled(false)
        .load()

    init {
        Database.connect(dataSource)
    }

    @BeforeTest
    fun setup() {
        flyway.clean()
        flyway.migrate()
    }
}
