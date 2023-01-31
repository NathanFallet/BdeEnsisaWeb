package me.nathanfallet.bdeensisa.models

import java.time.format.DateTimeFormatter
import kotlinx.datetime.*
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
        row.getOrNull(Events.start)?.toInstant(),
        row.getOrNull(Events.end)?.toInstant(),
        row.getOrNull(Events.topicId),
        topic
    )

    val formatted: String
        get() {
            if (end != start) {
                val startStr = DateTimeFormatter.ofPattern("'Du' dd/MM/yyyy 'à' HH:mm").format(
                    start?.toLocalDateTime(
                        TimeZone.currentSystemDefault()
                    )?.toJavaLocalDateTime()
                )
                val endStr = DateTimeFormatter.ofPattern("'au' dd/MM/yyyy 'à' HH:mm").format(
                    end?.toLocalDateTime(
                        TimeZone.currentSystemDefault()
                    )?.toJavaLocalDateTime()
                )
                return "$startStr $endStr"
            } else {
                return DateTimeFormatter.ofPattern("'Le' dd/MM/yyyy 'à' HH:mm").format(
                    start?.toLocalDateTime(
                        TimeZone.currentSystemDefault()
                    )?.toJavaLocalDateTime()
                )
            }
        }

}

object Events : Table() {

    val id = varchar("id", 32)
    val title = text("title")
    val content = text("content")
    val start = varchar("start", 255)
    val end = varchar("end", 255)
    val topicId = varchar("topic_id", 32)

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
        Events.deleteWhere {
            Op.build { Events.id eq id }
        }
        // TODO: Delete associated tickets and configurations
    }

}
