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

fun Route.adminTopics() {
    route("/topics") {
        get {
            getUser()?.let { user ->
                if (user.hasPermission("admin.topics.view")) {
                    val topics = Database.dbQuery {
                        Topics
                            .join(Users, JoinType.INNER, Topics.userId, Users.id)
                            .selectAll()
                            .orderBy(Topics.createdAt, SortOrder.DESC)
                            .map { Topic(it, User(it)) }
                    }
                    call.respond(FreeMarkerContent("admin/topics/list.ftl", mapOf(
                        "title" to "Affaires",
                        "topics" to topics,
                        "menu" to MenuItems.fetchAdmin(user)
                    )))
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/topics")
            }
        }
        get ("/new") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.topics.new")) {
                    call.respond(FreeMarkerContent("admin/topics/form.ftl", mapOf(
                        "title" to "Nouvelle affaire",
                        "menu" to MenuItems.fetchAdmin(user)
                    )))
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/topics")
            }
        }
        post ("/new") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.topics.new")) {
                    val params = call.receiveParameters()
                    val title = params["title"]
                    val content = params["content"]
                    val validated = params["validated"] == "on"
                    if (title != null && content != null) {
                        Database.dbQuery {
                            Topics.insert {
                                it[Topics.id] = Topics.generateId()
                                it[Topics.title] = title
                                it[Topics.content] = content
                                it[Topics.validated] = validated
                                it[Topics.userId] = user.id
                                it[Topics.createdAt] = Clock.System.now().toString()
                            }
                        }
                        call.respondRedirect("/admin/topics")
                    } else {
                        call.response.status(HttpStatusCode.BadRequest)
                        call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                    }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/topics")
            }
        }
        get ("/{id}") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.topics.edit")) {
                    call.parameters["id"]?.let { id ->
                        Database.dbQuery {
                            Topics
                                .join(Users, JoinType.INNER, Topics.userId, Users.id)
                                .select { Topics.id eq id }.map { Topic(it, User(it)) }
                                .singleOrNull()
                        }?.let { topic ->
                            call.respond(FreeMarkerContent("admin/topics/form.ftl", mapOf(
                                "title" to "Modifier une affaire",
                                "topic" to topic,
                                "menu" to MenuItems.fetchAdmin(user)
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
                call.respondRedirect("/account/login?redirect=/admin/topics")
            }
        }
        post ("/{id}") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.topics.edit")) {
                    call.parameters["id"]?.let { id ->
                        val params = call.receiveParameters()
                        val title = params["title"]
                        val content = params["content"]
                        val validated = params["validated"] == "on"
                        if (title != null && content != null) {
                            Database.dbQuery {
                                Topics.update({ Topics.id eq id }) {
                                    it[Topics.title] = title
                                    it[Topics.content] = content
                                    it[Topics.validated] = validated
                                }
                            }
                            call.respondRedirect("/admin/topics")
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
                call.respondRedirect("/account/login?redirect=/admin/topics")
            }
        }
        get ("/{id}/delete") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.topics.delete")) {
                    call.parameters["id"]?.let { id ->
                        Database.dbQuery {
                            Topics.deleteWhere {
                                Op.build { Topics.id eq id }
                            }
                        }
                        call.respondRedirect("/admin/topics")
                    } ?: run {
                        call.response.status(HttpStatusCode.NotFound)
                        call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                    }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/topics")
            }
        }
    }
}
