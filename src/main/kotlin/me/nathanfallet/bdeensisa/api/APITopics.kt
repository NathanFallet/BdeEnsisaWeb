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

fun Route.apiTopics() {
    route("/topics") {
        get {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
            val topics = Database.dbQuery {
                Topics
                    .select { Topics.validated eq true }
                    .orderBy(Topics.createdAt, SortOrder.DESC)
                    .limit(limit, offset)
                    .map { Topic(it) }
            }
            call.respond(topics)
        }
    }
}
