package me.nathanfallet.bdeensisa.models

import java.time.format.DateTimeFormatter
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
    val validated: Boolean?,
    val membersCount: Long?
) {

    constructor(
        row: ResultRow,
        membersCount: Long? = null
    ) : this(
        row[Clubs.id],
        row[Clubs.name],
        row.getOrNull(Clubs.description),
        row.getOrNull(Clubs.information),
        row.getOrNull(Clubs.createdAt)?.toInstant(),
        row.getOrNull(Clubs.validated),
        membersCount
    )

    val formatted: String
        get() = DateTimeFormatter.ofPattern("'le' dd/MM/yyyy 'à' HH:mm").format(
            createdAt?.toLocalDateTime(
                TimeZone.currentSystemDefault()
            )?.toJavaLocalDateTime()
        )

    val markdown: String
        get() = description?.let { Markdown.render(it) } ?: ""

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

fun Query.mapClubWithCount(): List<Club> {
    return map {
        val id = it[Clubs.id]
        val count = ClubMemberships.select {
            ClubMemberships.clubId eq id
        }.count()
        Club(it, count)
    }
}

fun Query.mapClubMembershipWithCount(): List<ClubMembership> {
    return map {
        val id = it[Clubs.id]
        val count = ClubMemberships.select {
            ClubMemberships.clubId eq id
        }.count()
        ClubMembership(it, null, Club(it, count))
    }
}
