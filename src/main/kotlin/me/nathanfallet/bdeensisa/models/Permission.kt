package me.nathanfallet.bdeensisa.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Permission(
    val userId: String,
    val permission: String,
    val user: User? = null
) {

    constructor(
        row: ResultRow,
        user: User? = null
    ): this(
        row[Permissions.userId],
        row[Permissions.permission],
        user
    )

}

object Permissions : Table() {

    val userId = varchar("user_id", 32)
    val permission = varchar("permission", 32)

    override val primaryKey = PrimaryKey(arrayOf(userId, permission))

}
