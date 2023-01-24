package me.nathanfallet.bdeensisa.models

import java.time.format.DateTimeFormatter
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Question(
    val id: String,
    val userId: String,
    val content: String?,
    val answer: String?,
    val createdAt: Instant?,
    val user: User? = null
) {

    constructor(
        row: ResultRow,
        user: User? = null
    ): this(
        row[Questions.id],
        row[Questions.userId],
        row.getOrNull(Questions.content),
        row.getOrNull(Questions.answer),
        row.getOrNull(Questions.createdAt)?.toInstant(),
        user
    )

    val formatted: String
        get() = DateTimeFormatter.ofPattern("'le' dd/MM/yyyy 'à' HH:mm").format(
            createdAt?.toLocalDateTime(
                TimeZone.currentSystemDefault()
            )?.toJavaLocalDateTime()
        )

}

object Questions : Table() {

    val id = varchar("id", 32)
    val userId = varchar("user_id", 32)
    val content = text("content")
    val answer = text("answer").nullable()
    val createdAt = varchar("created_at", 255)

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
