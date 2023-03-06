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
        post {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@post
            }
            if (!user.hasPermission("admin.events.create")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(mapOf("error" to "Not allowed to create events"))
                return@post
            }
            val upload = try {
                call.receive<EventUpload>()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid body"))
                return@post
            }
            upload.topicId?.let { topicId ->
                Database.dbQuery {
                    Topics.select { Topics.id eq topicId }.singleOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid topic"))
                    return@post
                }
            }
            val id = Events.generateId()
            Database.dbQuery {
                Events.insert {
                    it[Events.id] = id
                    it[Events.title] = upload.title!!
                    it[Events.content] = upload.content!!
                    it[Events.start] = upload.start!!.toInstant().toString()
                    it[Events.end] = upload.end!!.toInstant().toString()
                    it[Events.topicId] = upload.topicId!!
                }
            }
            val event = Database.dbQuery {
                Events
                    .join(Topics, JoinType.INNER, Events.topicId, Topics.id)
                    .select { Events.id eq id }.map { Event(it, Topic(it)) }
                    .singleOrNull()
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(mapOf("error" to "Event not found"))
                return@post
            }
            call.respond(event)
        }
        get ("/{id}") {
            val event = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Events
                        .join(Topics, JoinType.INNER, Events.topicId, Topics.id)
                        .select { Events.id eq id }.map { Event(it, Topic(it)) }
                        .singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(mapOf("error" to "Event not found"))
                return@get
            }
            call.respond(event)
        }
        put("/{id}") {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@put
            }
            if (!user.hasPermission("admin.events.edit")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(mapOf("error" to "Not allowed to edit events"))
                return@put
            }
            val event = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Events
                        .select { Events.id eq id }.map { Event(it) }
                        .singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(mapOf("error" to "Event not found"))
                return@put
            }
            val upload = try {
                call.receive<EventUpload>()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid body"))
                return@put
            }
            upload.topicId?.let { topicId ->
                Database.dbQuery {
                    Topics.select { Topics.id eq topicId }.singleOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid topic"))
                    return@put
                }
            }
            Database.dbQuery {
                Events.update({ Events.id eq event.id }) {
                    it[Events.title] = upload.title ?: event.title!!
                    it[Events.content] = upload.content ?: event.content!!
                    it[Events.start] = (upload.start?.toInstant() ?: event.start!!).toString()
                    it[Events.end] = (upload.end?.toInstant() ?: event.end!!).toString()
                    it[Events.topicId] = upload.topicId ?: event.topicId!!
                }
            }
            val newEvent = Database.dbQuery {
                Events
                    .join(Topics, JoinType.INNER, Events.topicId, Topics.id)
                    .select { Events.id eq event.id }.map { Event(it, Topic(it)) }
                    .singleOrNull()
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(mapOf("error" to "Event not found"))
                return@put
            }
            call.respond(newEvent)
        }
    }
}
