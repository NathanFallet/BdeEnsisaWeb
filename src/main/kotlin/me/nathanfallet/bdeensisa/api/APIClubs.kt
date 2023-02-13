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
                    .mapClubWithCount()
            }
            call.respond(clubs)
        }
        get("/{id}") {
            val club = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Clubs
                        .select { Clubs.id eq id }
                        .mapClubWithCount()
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
                        .mapClubMembershipWithCount()
                }
                call.respond(clubs)
            }
            post("/{id}/me") {
                val user = getUser() ?: run {
                    call.response.status(HttpStatusCode.Unauthorized)
                    call.respond(mapOf("error" to "Invalid user"))
                    return@post
                }
                if (user.cotisant == null) {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(mapOf("error" to "User is not allowed to join clubs"))
                    return@post
                }
                val club = call.parameters["id"]?.let { id ->
                    Database.dbQuery {
                        Clubs
                            .select { Clubs.id eq id and (Clubs.validated eq true) }
                            .map { Club(it) }
                            .firstOrNull()
                    }
                }?: run {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(mapOf("error" to "Club not found"))
                    return@post
                }
                Database.dbQuery {
                    ClubMemberships
                        .select { ClubMemberships.clubId eq club.id and (ClubMemberships.userId eq user.id) }
                        .map { ClubMembership(it) }
                        .firstOrNull()
                }?.let {
                    call.response.status(HttpStatusCode.Conflict)
                    call.respond(mapOf("error" to "User already in club"))
                    return@post
                }
                val membership = Database.dbQuery {
                    ClubMemberships.insert {
                        it[ClubMemberships.clubId] = club.id
                        it[ClubMemberships.userId] = user.id
                        it[ClubMemberships.role] = "member"
                    }.resultedValues?.firstOrNull()?.let { ClubMembership(it) }
                } ?: run {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(mapOf("error" to "Unable to create membership"))
                    return@post
                }
                call.respond(membership)
            }
            delete("/{id}/me") {
                val user = getUser() ?: run {
                    call.response.status(HttpStatusCode.Unauthorized)
                    call.respond(mapOf("error" to "Invalid user"))
                    return@delete
                }
                val club = call.parameters["id"]?.let { id ->
                    Database.dbQuery {
                        Clubs
                            .select { Clubs.id eq id and (Clubs.validated eq true) }
                            .map { Club(it) }
                            .firstOrNull()
                    }
                }?: run {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(mapOf("error" to "Club not found"))
                    return@delete
                }
                Database.dbQuery {
                    ClubMemberships
                        .select { ClubMemberships.clubId eq club.id and (ClubMemberships.userId eq user.id) }
                        .map { ClubMembership(it) }
                        .firstOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(mapOf("error" to "User not in club"))
                    return@delete
                }
                Database.dbQuery {
                    ClubMemberships.deleteWhere {
                        Op.build { ClubMemberships.clubId eq club.id and (ClubMemberships.userId eq user.id) }
                    }
                }
                call.respond(HttpStatusCode.NoContent)
            }
            put("/{id}/members") {
                val user = getUser() ?: run {
                    call.response.status(HttpStatusCode.Unauthorized)
                    call.respond(mapOf("error" to "Invalid user"))
                    return@put
                }
                val club = call.parameters["id"]?.let { id ->
                    Database.dbQuery {
                        Clubs
                            .select { Clubs.id eq id and (Clubs.validated eq true) }
                            .map { Club(it) }
                            .firstOrNull()
                    }
                }?: run {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(mapOf("error" to "Club not found"))
                    return@put
                }
                val membership = Database.dbQuery {
                    ClubMemberships
                        .select { ClubMemberships.clubId eq club.id and (ClubMemberships.userId eq user.id) }
                        .map { ClubMembership(it) }
                        .firstOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(mapOf("error" to "User not in club"))
                    return@put
                }
                if (membership.role != "admin") {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(mapOf("error" to "User is not allowed to manage members"))
                    return@put
                }
                val body = try {
                    call.receive<ClubMembership>()
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid body"))
                    return@put
                }
                val targetMembership = Database.dbQuery {
                    ClubMemberships
                        .select { ClubMemberships.clubId eq club.id and (ClubMemberships.userId eq body.userId) }
                        .map { ClubMembership(it) }
                        .firstOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(mapOf("error" to "User not in club"))
                    return@put
                }
                when (body.role) {
                    "admin", "member" -> {
                        Database.dbQuery {
                            ClubMemberships.update({
                                ClubMemberships.clubId eq club.id and (ClubMemberships.userId eq body.userId)
                            }) {
                                it[ClubMemberships.role] = body.role
                            }
                        }
                        call.respond(ClubMembership(
                            clubId = targetMembership.clubId,
                            userId = targetMembership.userId,
                            role = body.role
                        ))
                    }
                    "remove" -> {
                        Database.dbQuery {
                            ClubMemberships.deleteWhere {
                                Op.build { ClubMemberships.clubId eq club.id and (ClubMemberships.userId eq body.userId) }
                            }
                        }
                        call.respond(HttpStatusCode.NoContent)
                    }
                    else -> {
                        call.response.status(HttpStatusCode.BadRequest)
                        call.respond(mapOf("error" to "Invalid role"))
                    }
                }
            }
        }
    }
}
