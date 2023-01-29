package me.nathanfallet.bdeensisa.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class RegistrationRequest(
    val email: String,
    val code: String
) {

    constructor(
        row: ResultRow
    ) : this(
        row[RegistrationRequests.email],
        row[RegistrationRequests.code]
    )

}

object RegistrationRequests : Table() {

    val email = varchar("email", 255)
    val code = varchar("code", 32)

    override val primaryKey = PrimaryKey(email)

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
