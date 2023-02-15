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

fun Route.publicTopics() {
    route("/topics") {
        get {
            val topics = Database.dbQuery {
                Topics
                    .select { Topics.validated eq true }
                    .orderBy(Topics.createdAt, SortOrder.DESC)
                    .map { Topic(it) }
            }
            call.respond(FreeMarkerContent(
                "public/topics/list.ftl",
                mapOf(
                    "title" to "Affaires en cours",
                    "topics" to topics,
                    "menu" to MenuItems.fetch()
                )
            ))
        }
        get("/suggest") {
            getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/topics/suggest")
                return@get
            }
            call.respond(FreeMarkerContent(
                "public/topics/suggest.ftl",
                mapOf(
                    "title" to "Suggérer une affaire",
                    "menu" to MenuItems.fetch()
                )
            ))
        }
        post("/suggest") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/topics/suggest")
                return@post
            }
            Database.dbQuery {
                Topics.select {
                    Topics.userId eq user.id and
                    (Topics.createdAt greater Clock.System.now().minus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault()).toString())
                }.firstOrNull()
            }?.let {
                call.respond(FreeMarkerContent(
                    "public/topics/suggest.ftl",
                    mapOf(
                        "title" to "Suggérer une affaire",
                        "menu" to MenuItems.fetch(),
                        "error" to "Vous avez déjà suggéré une affaire il y a moins de 24h, veuillez attendre avant d'en suggérer une nouvelle."
                    )
                ))
                return@post
            }
            val params = call.receiveParameters()
            val title = params["title"]?.trim()
            val content = params["content"]
            if (title == null || content == null) {
                call.respond(FreeMarkerContent(
                    "public/topics/suggest.ftl",
                    mapOf(
                        "title" to "Suggérer une affaire",
                        "menu" to MenuItems.fetch(),
                        "error" to "Veuillez remplir tous les champs du formulaire."
                    )
                ))
                return@post
            }
            Database.dbQuery {
                Topics.insert {
                    it[Topics.id] = Topics.generateId()
                    it[Topics.title] = title
                    it[Topics.content] = content
                    it[Topics.validated] = false
                    it[Topics.userId] = user.id
                    it[Topics.createdAt] = Clock.System.now().toString()
                }
            }
            call.respond(FreeMarkerContent(
                "public/topics/suggest.ftl",
                mapOf(
                    "title" to "Suggérer une affaire",
                    "menu" to MenuItems.fetch(),
                    "success" to true
                )
            ))
        }
    }
}
