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
            getUser()?.let { user ->
                if (user.hasPermission("admin.events.view")) {
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
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/events")
            }
        }
        get ("/new") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.events.new")) {
                    val topics = Database.dbQuery {
                        Topics.selectAll().map { Topic(it) }
                    }
                    call.respond(FreeMarkerContent("admin/events/form.ftl", mapOf(
                        "title" to "Nouvelle évènement",
                        "menu" to MenuItems.fetchAdmin(user),
                        "topics" to topics
                    )))
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/events")
            }
        }
        post ("/new") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.events.new")) {
                    val params = call.receiveParameters()
                    val title = params["title"]
                    val content = params["content"]
                    val start = params["start"]?.toLocalDate()
                    val end = params["end"]?.toLocalDate()
                    val topic = params["topic"]
                    if (title != null && content != null && start != null && end != null && topic != null) {
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
                    } else {
                        call.response.status(HttpStatusCode.BadRequest)
                        call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                    }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/events")
            }
        }
        get ("/{id}") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.events.edit")) {
                    call.parameters["id"]?.let { id ->
                        Database.dbQuery {
                            Events
                                .join(Topics, JoinType.INNER, Events.topicId, Topics.id)
                                .select { Events.id eq id }.map { Event(it, Topic(it)) }
                                .singleOrNull()
                        }?.let { event ->
                            val topics = Database.dbQuery {
                                Topics.selectAll().map { Topic(it) }
                            }
                            call.respond(FreeMarkerContent("admin/events/form.ftl", mapOf(
                                "title" to "Modifier un évènement",
                                "event" to event,
                                "menu" to MenuItems.fetchAdmin(user),
                                "topics" to topics
                            )))
                        } ?: run {
                            call.response.status(HttpStatusCode.NotFound)
                            call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                        }
                    } ?: run {
                        call.response.status(HttpStatusCode.NotFound)
                        call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                    }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/events")
            }
        }
        post ("/{id}") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.events.edit")) {
                    call.parameters["id"]?.let { id ->
                        val params = call.receiveParameters()
                        val title = params["title"]
                        val content = params["content"]
                        val start = params["start"]?.toLocalDate()
                        val end = params["end"]?.toLocalDate()
                        val topic = params["topic"]
                        if (title != null && content != null && start != null && end != null && topic != null) {
                            Database.dbQuery {
                                Events.update({ Events.id eq id }) {
                                    it[Events.title] = title
                                    it[Events.content] = content
                                    it[Events.start] = start.toString()
                                    it[Events.end] = end.toString()
                                    it[Events.topicId] = topic
                                }
                            }
                            call.respondRedirect("/admin/events")
                        } else {
                            call.response.status(HttpStatusCode.BadRequest)
                            call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                        }
                    } ?: run {
                        call.response.status(HttpStatusCode.NotFound)
                        call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                    }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/events")
            }
        }
        get ("/{id}/delete") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.events.delete")) {
                    call.parameters["id"]?.let { id ->
                        Database.dbQuery {
                            Events.delete(id)
                        }
                        call.respondRedirect("/admin/events")
                    } ?: run {
                        call.response.status(HttpStatusCode.NotFound)
                        call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                    }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/events")
            }
        }
    }
}
