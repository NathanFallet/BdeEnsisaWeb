package me.nathanfallet.bdeensisa.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.*
import org.jetbrains.exposed.sql.*

fun Route.apiEvents() {
    route("/events") {
        get {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
            val events = Database.dbQuery {
                Events
                    .select { Events.end greater Clock.System.now().toString() }
                    .orderBy(Events.start)
                    .limit(limit, offset)
                    .map { Event(it) }
            }
            call.respond(events)
        }
    }
}
