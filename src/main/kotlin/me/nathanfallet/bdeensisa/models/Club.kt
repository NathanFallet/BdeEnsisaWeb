package me.nathanfallet.bdeensisa.models

import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import me.nathanfallet.bdeensisa.plugins.Markdown

@Serializable
data class Club(
    val id: String,
    val name: String,
    val description: String?,
    val information: String?,
    val createdAt: Instant?,
    val validated: Boolean?
) {

    constructor(
        row: ResultRow
    ) : this(
        row[Clubs.id],
        row[Clubs.name],
        row.getOrNull(Clubs.description),
        row.getOrNull(Clubs.information),
        row.getOrNull(Clubs.createdAt)?.toInstant(),
        row.getOrNull(Clubs.validated)
    )

}

object Clubs : Table() {

    val id = varchar("id", 32)
    val name = varchar("name", 255)
    val description = text("description")
    val information = text("information")
    val createdAt = varchar("created_at", 255)
    val validated = bool("validated")

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

    fun delete(id: String) {
        Clubs.deleteWhere {
            Op.build { Clubs.id eq id }
        }
        ClubMemberships.deleteWhere {
            Op.build { ClubMemberships.clubId eq id }
        }
    }

}
