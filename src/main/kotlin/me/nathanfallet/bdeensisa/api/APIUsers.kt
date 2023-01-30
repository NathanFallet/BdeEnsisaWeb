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

fun Route.apiUsers() {
    route("/users") {
        get("/me") {
            getUser()?.let { call.respond(it) } ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
            }
        }
        get {
            getUser()?.let { user ->
                if (user.hasPermission("admin.users.view")) {
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                    val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
                    Database.dbQuery {
                        Users
                            .join(
                                Cotisants, JoinType.LEFT, Users.id, Cotisants.userId,
                                additionalConstraint = { Cotisants.expiration greater Clock.System.now().toString() }
                            )
                            .slice(
                                Users.id,
                                Users.email,
                                Users.firstName,
                                Users.lastName,
                                Users.option,
                                Users.year,
                                Cotisants.userId,
                                Cotisants.expiration
                            )
                            .selectAll().limit(limit, offset).map {
                                User(it, it.getOrNull(Cotisants.userId)?.run { Cotisant(it) })
                            }
                    }.let { call.respond(it) }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(mapOf("error" to "Not allowed to view users"))
                }
            } ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
            }
        }
        get("/{id}") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.users.view")) {
                    call.parameters["id"]?.let { id ->
                        Database.dbQuery {
                            Users
                                .join(
                                    Cotisants, JoinType.LEFT, Users.id, Cotisants.userId,
                                    additionalConstraint = { Cotisants.expiration greater Clock.System.now().toString() }
                                )
                                .slice(
                                    Users.id,
                                    Users.email,
                                    Users.firstName,
                                    Users.lastName,
                                    Users.option,
                                    Users.year,
                                    Cotisants.userId,
                                    Cotisants.expiration
                                )
                                .select { Users.id eq id }.map {
                                    User(it, it.getOrNull(Cotisants.userId)?.run { Cotisant(it) })
                                }.singleOrNull()
                        }?.let { call.respond(it) } ?: run {
                            call.response.status(HttpStatusCode.NotFound)
                            call.respond(mapOf("error" to "User not found"))
                        }
                    } ?: run {
                        call.response.status(HttpStatusCode.BadRequest)
                        call.respond(mapOf("error" to "Invalid user id"))
                    }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(mapOf("error" to "Not allowed to view users"))
                }
            } ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
            }
        }
        
    }
}
