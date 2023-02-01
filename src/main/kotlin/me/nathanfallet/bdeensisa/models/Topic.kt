package me.nathanfallet.bdeensisa.models

import java.time.format.DateTimeFormatter
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import me.nathanfallet.bdeensisa.plugins.Markdown

@Serializable
data class Topic(
    val id: String,
    val userId: String,
    val title: String?,
    val content: String?,
    val createdAt: Instant?,
    val validated: Boolean?,
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
        row.getOrNull(Topics.createdAt)?.toInstant(),
        row.getOrNull(Topics.validated),
        user
    )

    val formatted: String
        get() = DateTimeFormatter.ofPattern("'le' dd/MM/yyyy 'à' HH:mm").format(
            createdAt?.toLocalDateTime(
                TimeZone.currentSystemDefault()
            )?.toJavaLocalDateTime()
        )

    val markdown: String
        get() = content?.let { Markdown.render(it) } ?: ""

}

object Topics : Table() {

    val id = varchar("id", 32)
    val userId = varchar("user_id", 32)
    val title = text("title")
    val content = text("content")
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
        Topics.deleteWhere {
            Op.build { Topics.id eq id }
        }
        Events.select { Events.topicId eq id }.forEach {
            Events.delete(it[Events.id])
        }
    }

}
