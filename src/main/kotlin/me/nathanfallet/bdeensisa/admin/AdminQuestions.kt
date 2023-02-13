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
import me.nathanfallet.bdeensisa.plugins.Notifications
import me.nathanfallet.bdeensisa.plugins.Notification
import org.jetbrains.exposed.sql.*

fun Route.adminQuestions() {
    route("/questions") {
        get {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/questions")
                return@get
            }
            if (!user.hasPermission("admin.questions.view")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
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
        }
        get ("/create") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/questions")
                return@get
            }
            if (!user.hasPermission("admin.questions.create")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            call.respond(FreeMarkerContent("admin/questions/form.ftl", mapOf(
                "title" to "Nouvelle question",
                "menu" to MenuItems.fetchAdmin(user)
            )))
        }
        post ("/create") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/questions")
                return@post
            }
            if (!user.hasPermission("admin.questions.create")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@post
            }
            val params = call.receiveParameters()
            val content = params["content"]
            val answer = params["answer"]
            if (content == null) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                return@post
            }
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
        }
        get ("/{id}") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/questions")
                return@get
            }
            if (!user.hasPermission("admin.questions.edit")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val question = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Questions
                        .join(Users, JoinType.INNER, Questions.userId, Users.id)
                        .select { Questions.id eq id }.map { Question(it, User(it)) }
                        .singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            call.respond(FreeMarkerContent("admin/questions/form.ftl", mapOf(
                "title" to "Modifier une question",
                "question" to question,
                "menu" to MenuItems.fetchAdmin(user)
            )))
        }
        post ("/{id}") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/questions")
                return@post
            }
            if (!user.hasPermission("admin.questions.edit")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@post
            }
            val question = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Questions
                        .select { Questions.id eq id }.map { Question(it) }
                        .singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@post
            }
            val params = call.receiveParameters()
            val content = params["content"]
            val answer = params["answer"]
            if (content == null) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                return@post
            }
            Database.dbQuery {
                Questions.update({ Questions.id eq question.id }) {
                    it[Questions.content] = content
                    it[Questions.answer] = answer?.let { if (it == "") null else it }
                }
            }
            if (question.answer == null && answer != null && answer != "") {
                Notifications.sendNotificationToUser(
                    question.userId,
                    Notification(
                        "${user.firstName} a répondu à votre question",
                        answer
                    )
                )
            }
            call.respondRedirect("/admin/questions")
        }
        get ("/{id}/delete") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/questions")
                return@get
            }
            if (!user.hasPermission("admin.questions.delete")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val question = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Questions
                        .select { Questions.id eq id }.map { Question(it) }
                        .singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            Database.dbQuery {
                Questions.deleteWhere {
                    Op.build { Questions.id eq question.id }
                }
            }
            call.respondRedirect("/admin/questions")
        }
    }
}
