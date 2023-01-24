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

fun Route.adminQuestions() {
    route("/questions") {
        get {
            getUser()?.let { user ->
                if (user.hasPermission("admin.questions.view")) {
                    val questions = Database.dbQuery {
                        Questions
                            .join(Users, JoinType.INNER, Questions.userId, Users.id)
                            .selectAll()
                            .orderBy(Questions.createdAt, SortOrder.DESC)
                            .map { Question(it, User(it)) }
                    }
                    call.respond(FreeMarkerContent("admin/questions/list.ftl", mapOf(
                        "title" to "Questions",
                        "questions" to questions,
                        "menu" to MenuItems.fetchAdmin(user)
                    )))
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/questions")
            }
        }
        get ("/new") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.questions.new")) {
                    call.respond(FreeMarkerContent("admin/questions/form.ftl", mapOf(
                        "title" to "Nouvelle question",
                        "menu" to MenuItems.fetchAdmin(user)
                    )))
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/questions")
            }
        }
        post ("/new") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.questions.new")) {
                    val params = call.receiveParameters()
                    val content = params["content"]
                    val answer = params["answer"]
                    if (content != null) {
                        Database.dbQuery {
                            Questions.insert {
                                it[Questions.id] = Questions.generateId()
                                it[Questions.content] = content
                                it[Questions.answer] = answer?.let { if (it == "") null else it }
                                it[Questions.userId] = user.id
                                it[Questions.createdAt] = Clock.System.now().toString()
                            }
                        }
                        call.respondRedirect("/admin/questions")
                    } else {
                        call.response.status(HttpStatusCode.BadRequest)
                        call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                    }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/questions")
            }
        }
        get ("/{id}") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.questions.edit")) {
                    call.parameters["id"]?.let { id ->
                        Database.dbQuery {
                            Questions
                                .join(Users, JoinType.INNER, Questions.userId, Users.id)
                                .select { Questions.id eq id }.map { Question(it, User(it)) }
                                .singleOrNull()
                        }?.let { question ->
                            call.respond(FreeMarkerContent("admin/questions/form.ftl", mapOf(
                                "title" to "Modifier une question",
                                "question" to question,
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
                call.respondRedirect("/account/login?redirect=/admin/questions")
            }
        }
        post ("/{id}") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.questions.edit")) {
                    call.parameters["id"]?.let { id ->
                        val params = call.receiveParameters()
                        val content = params["content"]
                        val answer = params["answer"]
                        if (content != null) {
                            Database.dbQuery {
                                Questions.update({ Questions.id eq id }) {
                                    it[Questions.content] = content
                                    it[Questions.answer] = answer?.let { if (it == "") null else it }
                                }
                            }
                            call.respondRedirect("/admin/questions")
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
                call.respondRedirect("/account/login?redirect=/admin/questions")
            }
        }
        get ("/{id}/delete") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.questions.delete")) {
                    call.parameters["id"]?.let { id ->
                        Database.dbQuery {
                            Questions.deleteWhere {
                                Op.build { Questions.id eq id }
                            }
                        }
                        call.respondRedirect("/admin/questions")
                    } ?: run {
                        call.response.status(HttpStatusCode.NotFound)
                        call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                    }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/questions")
            }
        }
    }
}
