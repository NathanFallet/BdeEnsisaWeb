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

fun Route.adminEvents() {
    route("/events") {
        get {
            val user = getUser()
            if (user == null) {
                call.respondRedirect("/account/login?redirect=/admin/events")
                return@get
            }
            if (!user.hasPermission("admin.events.view")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val events = Database.dbQuery {
                Events
                    .join(Topics, JoinType.INNER, Events.topicId, Topics.id)
                    .selectAll()
                    .orderBy(Events.start)
                    .map { Event(it, Topic(it)) }
            }
            call.respond(FreeMarkerContent("admin/events/list.ftl", mapOf(
                "title" to "Evènements",
                "events" to events,
                "menu" to MenuItems.fetchAdmin(user)
            )))
        }
        get ("/new") {
            val user = getUser()
            if (user == null) {
                call.respondRedirect("/account/login?redirect=/admin/events")
                return@get
            }
            if (!user.hasPermission("admin.events.new")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val topics = Database.dbQuery {
                Topics.selectAll().map { Topic(it) }
            }
            call.respond(FreeMarkerContent("admin/events/form.ftl", mapOf(
                "title" to "Nouvelle évènement",
                "menu" to MenuItems.fetchAdmin(user),
                "topics" to topics
            )))

        }
        post ("/new") {
            val user = getUser()
            if (user == null) {
                call.respondRedirect("/account/login?redirect=/admin/events")
                return@post
            }
            if (!user.hasPermission("admin.events.new")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@post
            }
            val params = call.receiveParameters()
            val title = params["title"]
            val content = params["content"]
            val start = params["start"]?.toInstant()
            val end = params["end"]?.toInstant()
            val topic = params["topic"]
            if (title == null || content == null || start == null || end == null || topic == null) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                return@post
            }
            Database.dbQuery {
                Events.insert {
                    it[Events.id] = Events.generateId()
                    it[Events.title] = title
                    it[Events.content] = content
                    it[Events.start] = start.toString()
                    it[Events.end] = end.toString()
                    it[Events.topicId] = topic
                }
            }
            call.respondRedirect("/admin/events")
        }
        get ("/{id}") {
            val user = getUser()
            if (user == null) {
                call.respondRedirect("/account/login?redirect=/admin/events")
                return@get
            }
            if (!user.hasPermission("admin.events.edit")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val event = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Events
                        .join(Topics, JoinType.INNER, Events.topicId, Topics.id)
                        .select { Events.id eq id }.map { Event(it, Topic(it)) }
                        .singleOrNull()
                }
            }
            if (event == null) {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            val topics = Database.dbQuery {
                Topics.selectAll().map { Topic(it) }
            }
            call.respond(FreeMarkerContent("admin/events/form.ftl", mapOf(
                "title" to "Modifier un évènement",
                "event" to event,
                "menu" to MenuItems.fetchAdmin(user),
                "topics" to topics
            )))
        }
        post ("/{id}") {
            val user = getUser()
            if (user == null) {
                call.respondRedirect("/account/login?redirect=/admin/events")
                return@post
            }
            if (!user.hasPermission("admin.events.edit")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@post
            }
            val event = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Events
                        .select { Events.id eq id }.map { Event(it) }
                        .singleOrNull()
                }
            }
            if (event == null) {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@post
            }
            val params = call.receiveParameters()
            val title = params["title"]
            val content = params["content"]
            val start = params["start"]?.toInstant()
            val end = params["end"]?.toInstant()
            val topic = params["topic"]
            if (title == null || content == null || start == null || end == null || topic == null) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                return@post
            }
            Database.dbQuery {
                Events.update({ Events.id eq event.id }) {
                    it[Events.title] = title
                    it[Events.content] = content
                    it[Events.start] = start.toString()
                    it[Events.end] = end.toString()
                    it[Events.topicId] = topic
                }
            }
            call.respondRedirect("/admin/events")
        }
        get ("/{id}/delete") {
            val user = getUser()
            if (user == null) {
                call.respondRedirect("/account/login?redirect=/admin/events")
                return@get
            }
            if (!user.hasPermission("admin.events.delete")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val event = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Events
                        .select { Events.id eq id }.map { Event(it) }
                        .singleOrNull()
                }
            }
            if (event == null) {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            Database.dbQuery {
                Events.delete(event.id)
            }
            call.respondRedirect("/admin/events")
        }
    }
}
