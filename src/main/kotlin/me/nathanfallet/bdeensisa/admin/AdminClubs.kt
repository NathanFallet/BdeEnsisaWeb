package me.nathanfallet.bdeensisa.admin

import kotlinx.datetime.*
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

fun Route.adminClubs() {
    route("/clubs") {
        get {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/clubs")
                return@get
            }
            if (!user.hasPermission("admin.clubs.view")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val clubs = Database.dbQuery {
                Clubs
                    .selectAll()
                    .orderBy(Clubs.createdAt, SortOrder.DESC)
                    .map { Club(it) }
            }
            call.respond(FreeMarkerContent("admin/clubs/list.ftl", mapOf(
                "title" to "Clubs",
                "clubs" to clubs,
                "menu" to MenuItems.fetchAdmin(user)
            )))
        }
        get ("/create") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/clubs")
                return@get
            }
            if (!user.hasPermission("admin.clubs.create")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            call.respond(FreeMarkerContent("admin/clubs/form.ftl", mapOf(
                "title" to "Nouveau club",
                "menu" to MenuItems.fetchAdmin(user)
            )))
        }
        post ("/create") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/clubs")
                return@post
            }
            if (!user.hasPermission("admin.clubs.create")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@post
            }
            val params = call.receiveParameters()
            val name = params["name"]
            val desciption = params["desciption"]
            val information = params["information"]
            val validated = params["validated"] == "on"
            if (name == null || desciption == null || information == null) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                return@post
            }
            Database.dbQuery {
                val id = Clubs.generateId()
                Clubs.insert {
                    it[Clubs.id] = id
                    it[Clubs.name] = name
                    it[Clubs.description] = description
                    it[Clubs.information] = information
                    it[Clubs.validated] = validated
                    it[Clubs.createdAt] = Clock.System.now().toString()
                }
                ClubMemberships.insert {
                    it[ClubMemberships.clubId] = id
                    it[ClubMemberships.userId] = user.id
                    it[ClubMemberships.role] = "owner"
                }
            }
            call.respondRedirect("/admin/clubs")
        }
        get ("/{id}") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/clubs")
                return@get
            }
            if (!user.hasPermission("admin.clubs.edit")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val club = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Clubs
                        .select { Clubs.id eq id }.map { Club(it) }
                        .singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            call.respond(FreeMarkerContent("admin/clubs/form.ftl", mapOf(
                "title" to "Modifier un club",
                "club" to club,
                "menu" to MenuItems.fetchAdmin(user)
            )))
        }
        post ("/{id}") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/clubs")
                return@post
            }
            if (!user.hasPermission("admin.clubs.edit")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@post
            }
            val club = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Clubs
                        .select { Clubs.id eq id }.map { Club(it) }
                        .singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@post
            }
            val params = call.receiveParameters()
            val name = params["name"]
            val desciption = params["desciption"]
            val information = params["information"]
            val validated = params["validated"] == "on"
            if (name == null || desciption == null || information == null) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                return@post
            }
            Database.dbQuery {
                Clubs.update({ Clubs.id eq club.id }) {
                    it[Clubs.name] = name
                    it[Clubs.description] = description
                    it[Clubs.information] = information
                    it[Clubs.validated] = validated
                }
            }
            call.respondRedirect("/admin/clubs")
        }
        get ("/{id}/delete") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/clubs")
                return@get
            }
            if (!user.hasPermission("admin.clubs.delete")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val club = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Clubs
                        .select { Clubs.id eq id }.map { Club(it) }
                        .singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            Database.dbQuery {
                Clubs.delete(club.id)
            }
            call.respondRedirect("/admin/clubs")
        }
    }
}
