package me.nathanfallet.bdeensisa.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Ticket(
    val id: String,
    val userId: String,
    val eventId: String,
    val user: User? = null,
    val event: Event? = null
) {

    constructor(
        row: ResultRow,
        user: User? = null,
        event: Event? = null
    ): this(
        row[Tickets.id],
        row[Tickets.userId],
        row[Tickets.eventId],
        user,
        event
    )

}

object Tickets : Table() {

    val id = varchar("id", 32)
    val userId = varchar("user_id", 32)
    val eventId = varchar("event_id", 32)

    override val primaryKey = PrimaryKey(id)

}
