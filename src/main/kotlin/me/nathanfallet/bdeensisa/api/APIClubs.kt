package me.nathanfallet.bdeensisa.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.*
import org.jetbrains.exposed.sql.*

fun Route.apiClubs() {
    route("/clubs") {
        get {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
            val clubs = Database.dbQuery {
                Clubs
                    .select { Clubs.validated eq true }
                    .orderBy(Clubs.createdAt, SortOrder.DESC)
                    .limit(limit, offset)
                    .map { Club(it) }
            }
            call.respond(clubs)
        }
        get("/{id}") {
            val club = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Clubs
                        .select { Clubs.id eq id }
                        .map { Club(it) }
                        .firstOrNull()
                }
            }
            if (club == null) {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(mapOf("error" to "Club not found"))
                return@get
            }
            call.respond(club)
        }
        get("/{id}/members") {
            val members = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    ClubMemberships
                        .join(Users, JoinType.INNER, ClubMemberships.userId, Users.id)
                        .select { ClubMemberships.clubId eq id }
                        .map { ClubMembership(it, User(it)) }
                }
            }
            if (members == null) {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(mapOf("error" to "Club not found"))
                return@get
            }
            call.respond(members)
        }
        authenticate("api-jwt") {
            get("/me") {
                val user = getUser() ?: run {
                    call.response.status(HttpStatusCode.Unauthorized)
                    call.respond(mapOf("error" to "Invalid user"))
                    return@get
                }
                val clubs = Database.dbQuery {
                    ClubMemberships
                        .join(Clubs, JoinType.INNER, ClubMemberships.clubId, Clubs.id)
                        .select { ClubMemberships.userId eq user.id }
                        .orderBy(Clubs.createdAt, SortOrder.DESC)
                        .map { ClubMembership(it, null, Club(it)) }
                }
                call.respond(clubs)
            }
        }
    }
}
