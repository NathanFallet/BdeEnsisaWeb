package me.nathanfallet.bdeensisa.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.*
import org.jetbrains.exposed.sql.*

@Serializable
data class LoginAuthorize(
    val code: String,
    val user: String,
    val expiration: Instant
) {

    constructor(row: ResultRow): this(
        row[LoginAuthorizes.code],
        row[LoginAuthorizes.user],
        row[LoginAuthorizes.expiration].toInstant()
    )

}

object LoginAuthorizes: Table() {

    val code = varchar("code", 32)
    val user = varchar("user", 32)
    val expiration = varchar("expiration", 255)

    override val primaryKey = PrimaryKey(code)

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
