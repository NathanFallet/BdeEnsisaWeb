package me.nathanfallet.bdeensisa.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import me.nathanfallet.bdeensisa.database.Database

@Serializable
data class MenuItem(
    val id: String,
    val title: String,
    val url: String,
    val parent: String?,
    val children: List<MenuItem>
) {

    constructor(
        row: ResultRow,
        children: List<MenuItem>? = null
    ) : this(
        row[MenuItems.id],
        row[MenuItems.title],
        row[MenuItems.url],
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
    val parent = varchar("parent", 32).nullable()

    override val primaryKey = PrimaryKey(id)

    fun generateId(): String {
        val charPool: List<Char> = ('a'..'z') + ('0'..'9')
        val candidate = List(32) { charPool.random() }.joinToString("")
        if (select { MenuItems.id eq candidate }.count() > 0) {
            return generateId()
        } else {
            return candidate
        }
    }

    suspend fun fetch(): List<MenuItem> {
        return Database.dbQuery {
            MenuItems.select {
                MenuItems.parent eq null
            }.map { row ->
                MenuItem(row, MenuItems.select {
                    MenuItems.parent eq row[MenuItems.id]
                }.map { MenuItem(it, null) })
            }
        }
    }

    suspend fun fetchAdmin(user: User): List<MenuItem> {
        var items = mutableListOf<MenuItem>()

        if (user.hasPermission("admin.dashboard")) {
            items.add(MenuItem("dashboard", "Tableau de bord", "/admin/dashboard", null, emptyList()))
        }
        if (user.hasPermission("admin.menu.view")) {
            items.add(MenuItem("menu", "Menu", "/admin/menu", null, emptyList()))
        }
        if (user.hasPermission("admin.pages.view")) {
            items.add(MenuItem("pages", "Pages", "/admin/pages", null, emptyList()))
        }

        return items
    }

}
