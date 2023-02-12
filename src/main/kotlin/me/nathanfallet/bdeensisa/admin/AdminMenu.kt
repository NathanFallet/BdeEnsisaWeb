package me.nathanfallet.bdeensisa.admin

import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import me.nathanfallet.bdeensisa.account.getUser
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.*
import org.jetbrains.exposed.sql.*

fun Route.adminMenu() {
    route("/menu") {
        get {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/menu")
                return@get
            }
            if (!user.hasPermission("admin.menu.view")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val menu = Database.dbQuery {
                MenuItems.selectAll().orderBy(MenuItems.position).map { MenuItem(it) }
            }
            call.respond(FreeMarkerContent("admin/menu/list.ftl", mapOf(
                "title" to "Menu",
                "menuitems" to menu,
                "menu" to MenuItems.fetchAdmin(user)
            )))
        }
        get ("/create") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/menu")
                return@get
            }
            if (!user.hasPermission("admin.menu.create")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val parents = Database.dbQuery {
                MenuItems.select {
                    MenuItems.parent eq null
                }.map { MenuItem(it) }
            }
            call.respond(FreeMarkerContent("admin/menu/form.ftl", mapOf(
                "title" to "Nouvel élément de menu",
                "menu" to MenuItems.fetchAdmin(user),
                "parents" to parents
            )))
        }
        post ("/create") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/menu")
                return@post
            }
            if (!user.hasPermission("admin.menu.create")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@post
            }
            val params = call.receiveParameters()
            val title = params["title"]
            val url = params["url"]
            val position = params["position"]
            val parent = params["parent"]
            if (title == null || url == null || position == null) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                return@post
            }
            Database.dbQuery {
                MenuItems.insert {
                    it[MenuItems.id] = MenuItems.generateId()
                    it[MenuItems.title] = title
                    it[MenuItems.url] = url
                    it[MenuItems.position] = position.toInt()
                    it[MenuItems.parent] = parent?.let { if (it == "") null else it }
                }
            }
            call.respondRedirect("/admin/menu")
        }
        get ("/{id}") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/menu")
                return@get
            }
            if (!user.hasPermission("admin.menu.edit")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val item = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    MenuItems.select { MenuItems.id eq id }.map { MenuItem(it) }.singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            val parents = Database.dbQuery {
                MenuItems.select {
                    MenuItems.parent eq null
                }.map { MenuItem(it) }
            }
            call.respond(FreeMarkerContent("admin/menu/form.ftl", mapOf(
                "title" to "Modifier une page",
                "item" to item,
                "menu" to MenuItems.fetchAdmin(user),
                "parents" to parents
            )))
        }
        post ("/{id}") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/menu")
                return@post
            }
            if (!user.hasPermission("admin.menu.edit")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@post
            }
            val item = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    MenuItems.select { MenuItems.id eq id }.map { MenuItem(it) }.singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@post
            }
            val params = call.receiveParameters()
            val title = params["title"]
            val url = params["url"]
            val position = params["position"]
            val parent = params["parent"]
            if (title == null || url == null || position == null) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                return@post
            }
            Database.dbQuery {
                MenuItems.update({ MenuItems.id eq item.id }) {
                    it[MenuItems.title] = title
                    it[MenuItems.url] = url
                    it[MenuItems.position] = position.toInt()
                    it[MenuItems.parent] = parent?.let { if (it == "") null else it }
                }
            }
            call.respondRedirect("/admin/menu")
        }
        get ("/{id}/delete") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/menu")
                return@get
            }
            if (!user.hasPermission("admin.menu.delete")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val item = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    MenuItems.select { MenuItems.id eq id }.map { MenuItem(it) }.singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            Database.dbQuery {
                MenuItems.deleteWhere {
                    Op.build { MenuItems.id eq item.id }
                }
            }
            call.respondRedirect("/admin/menu")
        }
    }
}
