package me.nathanfallet.bdeensisa.models

import java.time.format.DateTimeFormatter
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class ClubMembership(
    val userId: String,
    val clubId: String,
    val role: String,
    val user: User? = null,
    val club: Club? = null
) {

    constructor(
        row: ResultRow,
        user: User? = null,
        club: Club? = null
    ): this(
        row[ClubMemberships.userId],
        row[ClubMemberships.clubId],
        row[ClubMemberships.role],
        user,
        club
    )

}

object ClubMemberships : Table() {

    val userId = varchar("user_id", 32)
    val clubId = varchar("club_id", 32)
    val role = varchar("role", 255)

    override val primaryKey = PrimaryKey(userId)

}
