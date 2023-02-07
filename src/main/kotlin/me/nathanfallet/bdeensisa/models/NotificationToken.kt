package me.nathanfallet.bdeensisa.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class NotificationToken(
    val token: String,
    val userId: String?,
    val expiration: Instant,
    val user: User? = null
) {

    constructor(
        row: ResultRow,
        user: User? = null
    ): this(
        row[NotificationsTokens.token],
        row.getOrNull(NotificationsTokens.userId),
        Instant.parse(row[NotificationsTokens.expiration]),
        user
    )

}

object NotificationsTokens : Table() {

    val token = varchar("token", 255)
    val userId = varchar("user_id", 32)
    val expiration = varchar("expiration", 255)

    override val primaryKey = PrimaryKey(token)

}

@Serializable
data class NotificationTokenUpload(
    val token: String
)
