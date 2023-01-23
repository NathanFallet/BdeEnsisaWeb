package me.nathanfallet.bdeensisa.public

import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.*
import org.jetbrains.exposed.sql.*

fun Route.publicTopics() {
    route("/topics") {
        get {
            val topics = Database.dbQuery {
                Topics.select { Topics.validated eq true }.map { Topic(it) }
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
        get("/{id}") {
            
        }
    }
}
