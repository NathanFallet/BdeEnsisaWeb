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

fun Route.publicEvents() {
    route("/events") {
        get {
            val events = Database.dbQuery {
                Events.selectAll().map { Event(it) }
            }
            val nextEvents = Database.dbQuery {
                Events
                    .select { Events.end greater Clock.System.now().toString() }
                    .orderBy(Events.start)
                    .limit(5)
                    .map { Event(it) }
            }
            call.respond(FreeMarkerContent(
                "public/events/list.ftl",
                mapOf(
                    "title" to "Evènements",
                    "calendar" to events,
                    "events" to nextEvents,
                    "menu" to MenuItems.fetch()
                )
            ))
        }
    }
}
