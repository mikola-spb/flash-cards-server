package com.khasanov.flashcards.db

import com.khasanov.flashcards.user.UserResponse
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class UserRepository {

    suspend fun findByExternalId(externalId: String): UserResponse? = newSuspendedTransaction {
        UserTable.selectAll()
            .where { UserTable.externalId eq externalId }
            .singleOrNull()
            ?.toUserResponse()
    }

    suspend fun create(externalId: String): UserResponse = newSuspendedTransaction {
        val inserted = UserTable.insert {
            it[UserTable.externalId] = externalId
        }

        UserTable.selectAll()
            .where { UserTable.id eq inserted[UserTable.id] }
            .single()
            .toUserResponse()
    }

    private fun ResultRow.toUserResponse() = UserResponse(
        id = this[UserTable.id].toString(),
        externalId = this[UserTable.externalId],
        createdAt = this[UserTable.createdAt].toString(),
    )
}
