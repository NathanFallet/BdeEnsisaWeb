package me.nathanfallet.bdeensisa.models

import kotlinx.serialization.Serializable
import me.nathanfallet.bdeensisa.database.Database
import org.jetbrains.exposed.sql.*

@Serializable
data class User(
    val id: String,
    val email: String?,
    val password: String?,
    val firstName: String?,
    val lastName: String?,
    val option: String?,
    val year: String?,
    val cotisant: Cotisant? = null,
    var permissions: List<String>? = null
) {

    constructor(
        row: ResultRow,
        cotisant: Cotisant? = null
    ) : this(
        row[Users.id],
        row.getOrNull(Users.email),
        row.getOrNull(Users.password),
        row.getOrNull(Users.firstName),
        row.getOrNull(Users.lastName),
        row.getOrNull(Users.option),
        row.getOrNull(Users.year),
        cotisant
    )

    suspend fun hasPermission(permission: String): Boolean {
        // Cache permissions to avoid multiple queries
        if (permissions == null) {
            permissions = Database.dbQuery {
                Permissions.select { Permissions.userId eq id }.map { it[Permissions.permission] }
            }
        }

        // Construct allowed permissions
        // i.e. for "admin.users.view" we will check "admin.users.view", "admin.users.*" and "admin.*"
        val allowedPermissions = mutableListOf(permission)
        while (allowedPermissions.last().replace(".*", "").contains('.')) {
            allowedPermissions.add(allowedPermissions.last().replace(".*", "").substringBeforeLast('.') + ".*")
        }
        return permissions?.any { allowedPermissions.contains(it) } ?: false
    }

    val description: String
        get() = {
            val optionStr = when(option) {
                "ir" -> "Informatique et Réseaux"
                "ase" -> "Automatique et Systèmes embarqués"
                "meca" -> "Mécanique"
                "tf" -> "Textile et Fibres"
                "gi" -> "Génie Industriel"
                else -> "Inconnu"
            }
            year + " - " + optionStr
        }()

}

object Users : Table() {

    val id = varchar("id", 32)
    val email = varchar("email", 255)
    val password = varchar("password", 255)
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val option = varchar("option", 255)
    val year = varchar("year", 255)

    override val primaryKey = PrimaryKey(id)

    fun generateId(): String {
        val charPool: List<Char> = ('a'..'z') + ('0'..'9')
        val candidate = List(32) { charPool.random() }.joinToString("")
        if (select { id eq candidate }.count() > 0) {
            return generateId()
        } else {
            return candidate
        }
    }

}

@Serializable data class UserToken(val token: String, val user: User)
