package me.nathanfallet.bdeensisa.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.*
import org.jetbrains.exposed.sql.*

@Serializable
data class PasswordRequest(
    val userId: String,
    val code: String,
    val expiration: Instant
) {

    constructor(
        row: ResultRow
    ) : this(
        row[PasswordRequests.userId],
        row[PasswordRequests.code],
        row[PasswordRequests.expiration].toInstant()
    )

}

object PasswordRequests : Table() {

    val userId = varchar("user_id", 32)
    val code = varchar("code", 32)
    val expiration = varchar("expiration", 255)

    override val primaryKey = PrimaryKey(userId)

    fun generateCode(): String {
        val charPool: List<Char> = ('a'..'z') + ('0'..'9')
        val candidate = List(32) { charPool.random() }.joinToString("")
        if (select { code eq candidate }.count() > 0) {
            return generateCode()
        } else {
            return candidate
        }
    }

}
