package me.nathanfallet.bdeensisa.public

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

fun Route.publicQuestions() {
    route("/questions") {
        get {
            val questions = Database.dbQuery {
                Questions.selectAll().map { Question(it) }
            }
            call.respond(FreeMarkerContent(
                "public/questions/list.ftl",
                mapOf(
                    "title" to "Questions",
                    "questions" to questions,
                    "menu" to MenuItems.fetch()
                )
            ))
        }
        get("/ask") {
            getUser()?.let {
                call.respond(FreeMarkerContent(
                    "public/questions/ask.ftl",
                    mapOf(
                        "title" to "Poser une question",
                        "menu" to MenuItems.fetch()
                    )
                ))
            } ?: run {
                call.respondRedirect("/account/login?redirect=/questions/ask")
            }
        }
        post("/ask") {
            getUser()?.let { user ->
                val params = call.receiveParameters()
                val content = params["content"]
                if (content != null) {
                    Database.dbQuery {
                        Questions.insert {
                            it[Questions.id] = Questions.generateId()
                            it[Questions.content] = content
                            it[Questions.userId] = user.id
                            it[Questions.createdAt] = Clock.System.now().toString()
                        }
                    }
                    call.respond(FreeMarkerContent(
                        "public/questions/ask.ftl",
                        mapOf(
                            "title" to "Poser une question",
                            "menu" to MenuItems.fetch(),
                            "success" to true
                        )
                    ))
                } else {
                    call.respond(FreeMarkerContent(
                        "public/questions/ask.ftl",
                        mapOf(
                            "title" to "Poser une question",
                            "menu" to MenuItems.fetch(),
                            "error" to true
                        )
                    ))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/questions/ask")
            }
        }
    }
}
