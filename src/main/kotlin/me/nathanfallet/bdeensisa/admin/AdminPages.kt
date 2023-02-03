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

fun Route.adminPages() {
    route("/pages") {
        get {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/pages")
                return@get
            }
            if (!user.hasPermission("admin.pages.view")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val pages = Database.dbQuery {
                Pages.selectAll().map { Page(it) }
            }
            call.respond(FreeMarkerContent("admin/pages/list.ftl", mapOf(
                "title" to "Pages",
                "pages" to pages,
                "menu" to MenuItems.fetchAdmin(user)
            )))
        }
        get ("/new") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/pages")
                return@get
            }
            if (!user.hasPermission("admin.pages.new")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            call.respond(FreeMarkerContent("admin/pages/form.ftl", mapOf(
                "title" to "Nouvelle page",
                "menu" to MenuItems.fetchAdmin(user)
            )))
        }
        post ("/new") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/pages")
                return@post
            }
            if (!user.hasPermission("admin.pages.new")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@post
            }
            val params = call.receiveParameters()
            val url = params["url"]
            val title = params["title"]
            val content = params["content"]
            val home = params["home"] == "on"
            if (url == null || title == null || content == null) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                return@post
            }
            Database.dbQuery {
                Pages.insert {
                    it[Pages.id] = Pages.generateId()
                    it[Pages.url] = url
                    it[Pages.title] = title
                    it[Pages.content] = content
                    it[Pages.home] = home
                }
            }
            call.respondRedirect("/admin/pages")
        }
        get ("/{id}") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/pages")
                return@get
            }
            if (!user.hasPermission("admin.pages.edit")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val page = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Pages.select { Pages.id eq id }.map { Page(it) }.singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            call.respond(FreeMarkerContent("admin/pages/form.ftl", mapOf(
                "title" to "Modifier une page",
                "page" to page,
                "menu" to MenuItems.fetchAdmin(user)
            )))
        }
        post ("/{id}") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/pages")
                return@post
            }
            if (!user.hasPermission("admin.pages.edit")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@post
            }
            val page = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Pages.select { Pages.id eq id }.map { Page(it) }.singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@post
            }
            val params = call.receiveParameters()
            val url = params["url"]
            val title = params["title"]
            val content = params["content"]
            val home = params["home"] == "on"
            if (url == null || title == null || content == null) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                return@post
            }
            Database.dbQuery {
                Pages.update({ Pages.id eq page.id }) {
                    it[Pages.url] = url
                    it[Pages.title] = title
                    it[Pages.content] = content
                    it[Pages.home] = home
                }
            }
            call.respondRedirect("/admin/pages")
        }
        get ("/{id}/delete") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/pages")
                return@get
            }
            if (!user.hasPermission("admin.pages.delete")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val page = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Pages.select { Pages.id eq id }.map { Page(it) }.singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            Database.dbQuery {
                Pages.deleteWhere {
                    Op.build { Pages.id eq page.id }
                }
            }
            call.respondRedirect("/admin/pages")
        }
    }
}
