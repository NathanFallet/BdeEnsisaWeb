package me.nathanfallet.bdeensisa.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Event(
    val id: String,
    val title: String?,
    val content: String?,
    val start: Instant?,
    val end: Instant?,
    val topicId: String?,
    val topic: Topic? = null
) {

    constructor(
        row: ResultRow,
        topic: Topic? = null
    ): this(
        row[Events.id],
        row.getOrNull(Events.title),
        row.getOrNull(Events.content),
        row.getOrNull(Events.start)?.let { Instant.parse(it) },
        row.getOrNull(Events.end)?.let { Instant.parse(it) },
        row.getOrNull(Events.topicId),
        topic
    )

}

object Events : Table() {

    val id = varchar("id", 32)
    val title = text("title")
    val content = text("content")
    val start = varchar("start", 255)
    val end = varchar("end", 255)
    val topicId = varchar("topic_id", 32)

    override val primaryKey = PrimaryKey(id)

}
