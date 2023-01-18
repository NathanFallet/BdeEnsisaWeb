package me.nathanfallet.bdeensisa.models

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class User(
        val id: String,
        val email: String?,
        val password: String?,
        val first_name: String?,
        val last_name: String?,
        val option: String?
) {

    constructor(
            row: ResultRow
    ) : this(
            row[Users.id],
            row.getOrNull(Users.email),
            row.getOrNull(Users.password),
            row.getOrNull(Users.first_name),
            row.getOrNull(Users.last_name),
            row.getOrNull(Users.option)
    )

}

object Users : Table() {

    val id = varchar("id", 32)
    val email = varchar("email", 255)
    val password = varchar("password", 255)
    val first_name = varchar("first_name", 255)
    val last_name = varchar("last_name", 255)
    val option = varchar("option", 255)

    override val primaryKey = PrimaryKey(id)

    fun generateId(): String {
        val charPool: List<Char> = ('a'..'z') + ('0'..'9')
        val candidate = List(32) { charPool.random() }.joinToString("")
        if (select { Users.id eq candidate }.count() > 0) {
            return generateId()
        } else {
            return candidate
        }
    }

}

@Serializable data class UserToken(val token: String, val user: User)
