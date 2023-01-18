package me.nathanfallet.bdeensisa.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Cotisant(
    val userId: String,
    val expiration: Instant,
    val user: User? = null
) {

    constructor(
        row: ResultRow,
        user: User? = null
    ): this(
        row[Cotisants.userId],
        Instant.parse(row[Cotisants.expiration]),
        user
    )

}

object Cotisants : Table() {

    val userId = varchar("user_id", 32)
    val expiration = varchar("expiration", 255)

    override val primaryKey = PrimaryKey(userId)

}
