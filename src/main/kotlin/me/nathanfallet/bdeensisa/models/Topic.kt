package me.nathanfallet.bdeensisa.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Topic(
    val id: String,
    val userId: String,
    val title: String?,
    val content: String?,
    val user: User? = null
) {

    constructor(
        row: ResultRow,
        user: User? = null
    ): this(
        row[Topics.id],
        row[Topics.userId],
        row.getOrNull(Topics.title),
        row.getOrNull(Topics.content),
        user
    )

}

object Topics : Table() {

    val id = varchar("id", 32)
    val userId = varchar("user_id", 32)
    val title = text("title")
    val content = text("content")

    override val primaryKey = PrimaryKey(id)

}
