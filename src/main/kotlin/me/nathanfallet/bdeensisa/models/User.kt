package me.nathanfallet.bdeensisa.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class User(
    val id: String,
    val email: String?,
    val password: String?,
    val firstName: String?,
    val lastName: String?,
    val option: String?
) {

    constructor(
        row: ResultRow
    ) : this(
        row[Users.id],
        row.getOrNull(Users.email),
        row.getOrNull(Users.password),
        row.getOrNull(Users.firstName),
        row.getOrNull(Users.lastName),
        row.getOrNull(Users.option)
    )

}

object Users : Table() {

    val id = varchar("id", 32)
    val email = varchar("email", 255)
    val password = varchar("password", 255)
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
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
