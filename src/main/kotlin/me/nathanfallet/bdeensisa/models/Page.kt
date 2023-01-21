package me.nathanfallet.bdeensisa.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Page(
    val id: String,
    val url: String,
    val title: String?,
    val content: String?,
    val home: Boolean?
) {

    constructor(
        row: ResultRow
    ) : this(
        row[Pages.id],
        row[Pages.url],
        row.getOrNull(Pages.title),
        row.getOrNull(Pages.content),
        row.getOrNull(Pages.home)
    )

}

object Pages : Table() {

    val id = varchar("id", 32)
    val url = varchar("url", 255)
    val title = text("title")
    val content = text("content")
    val home = bool("home")

    override val primaryKey = PrimaryKey(id)

    fun generateId(): String {
        val charPool: List<Char> = ('a'..'z') + ('0'..'9')
        val candidate = List(32) { charPool.random() }.joinToString("")
        if (select { Pages.id eq candidate }.count() > 0) {
            return generateId()
        } else {
            return candidate
        }
    }

}
