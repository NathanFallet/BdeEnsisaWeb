package me.nathanfallet.bdeensisa.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import me.nathanfallet.bdeensisa.database.Database

@Serializable
data class MenuItem(
    val id: String,
    val title: String,
    val url: String,
    val position: Int = 0,
    val parent: String? = null,
    val children: List<MenuItem> = emptyList()
) {

    constructor(
        row: ResultRow,
        children: List<MenuItem>? = null
    ) : this(
        row[MenuItems.id],
        row[MenuItems.title],
        row[MenuItems.url],
        row[MenuItems.position],
        row.getOrNull(MenuItems.parent),
        children ?: emptyList()
    )

    val short: String
        get() = title.firstOrNull()?.toString() ?: ""

}

object MenuItems : Table() {

    val id = varchar("id", 32)
    val title = text("title")
    val url = varchar("url", 255)
    val position = integer("position")
    val parent = varchar("parent", 32).nullable()

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

    suspend fun fetch(): List<MenuItem> {
        return Database.dbQuery {
            MenuItems.select {
                MenuItems.parent eq null
            }.orderBy(MenuItems.position).map { row ->
                MenuItem(row, MenuItems.select {
                    MenuItems.parent eq row[MenuItems.id]
                }.orderBy(MenuItems.position).map { MenuItem(it, null) })
            }
        }
    }

    suspend fun fetchAdmin(user: User): List<MenuItem> {
        var items = mutableListOf<MenuItem>()

        if (user.hasPermission("admin.dashboard")) {
            items.add(MenuItem("dashboard", "Tableau de bord", "/admin"))
        }
        if (user.hasPermission("admin.menu.view")) {
            items.add(MenuItem("menu", "Menu", "/admin/menu"))
        }
        if (user.hasPermission("admin.pages.view")) {
            items.add(MenuItem("pages", "Pages", "/admin/pages"))
        }
        if (user.hasPermission("admin.users.view")) {
            items.add(MenuItem("users", "Utilisateurs", "/admin/users"))
        }
        if (user.hasPermission("admin.topics.view")) {
            items.add(MenuItem("topics", "Affaires", "/admin/topics"))
        }
        if (user.hasPermission("admin.questions.view")) {
            items.add(MenuItem("questions", "Questions", "/admin/questions"))
        }
        if (user.hasPermission("admin.events.view")) {
            items.add(MenuItem("events", "Evènements", "/admin/events"))
        }
        if (user.hasPermission("admin.uploads.view")) {
            items.add(MenuItem("uploads", "Téléchargements", "/admin/uploads"))
        }
        if (user.hasPermission("admin.clubs.view")) {
            items.add(MenuItem("clubs", "Clubs", "/admin/clubs"))
        }

        return items
    }

}
