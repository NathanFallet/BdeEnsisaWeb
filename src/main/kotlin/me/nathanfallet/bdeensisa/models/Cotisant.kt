package me.nathanfallet.bdeensisa.models

import java.time.format.DateTimeFormatter
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Cotisant(
    val userId: String,
    val expiration: LocalDate,
    val updatedAt: Instant?,
    val user: User? = null
) {

    constructor(
        row: ResultRow,
        user: User? = null
    ): this(
        row[Cotisants.userId],
        row[Cotisants.expiration].toLocalDate(),
        row.getOrNull(Cotisants.updatedAt)?.toInstant(),
        user
    )

    val formatted: String
        get() = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(
            expiration.toJavaLocalDate()
        )

}

object Cotisants : Table() {

    val userId = varchar("user_id", 32)
    val expiration = varchar("expiration", 255)
    val updatedAt = varchar("updated_at", 255)

    override val primaryKey = PrimaryKey(userId)

}
