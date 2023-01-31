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
        put("/me") {
            getUser()?.let { user ->
                val upload = try {
                    call.receive<UserUpload>()
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid body"))
                    return@put
                }
                upload.option?.let { option ->
                    if (!listOf("ir", "ase", "meca", "tf", "gi").contains(option)) {
                        call.response.status(HttpStatusCode.BadRequest)
                        call.respond(mapOf(
                            "error" to "Option invalide !"
                        ))
                        return@put
                    }
                }
                upload.year?.let { year ->
                    if (!listOf("1A", "2A", "3A", "other").contains(year)) {
                        call.response.status(HttpStatusCode.BadRequest)
                        call.respond(mapOf(
                            "error" to "Année invalide !"
                        ))
                        return@put
                    }
                }
                Database.dbQuery {
                    Users.update({ Users.id eq user.id }) {
                        it[Users.firstName] = upload.firstName ?: user.firstName!!
                        it[Users.lastName] = upload.lastName ?: user.lastName!!
                        it[Users.option] = upload.option ?: user.option!!
                        it[Users.year] = upload.year ?: user.year!!
                    }
                }
                getUser()?.let { call.respond(it) } ?: run {
                    call.response.status(HttpStatusCode.Unauthorized)
                    call.respond(mapOf("error" to "Invalid user"))
                }
            } ?: run {
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
                        Users.customJoin().selectAll().limit(limit, offset).mapUser(false)
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
                            Users.customJoin().select { Users.id eq id }.mapUser(true).singleOrNull()
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
        put("/{id}") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.users.edit")) {
                    call.parameters["id"]?.let { id ->
                        Database.dbQuery {
                            Users.customJoin().select { Users.id eq id }.mapUser(false).singleOrNull()
                        }?.let { selectedUser ->
                            val upload = try {
                                call.receive<UserUpload>()
                            } catch (e: Exception) {
                                call.response.status(HttpStatusCode.BadRequest)
                                call.respond(mapOf("error" to "Invalid body"))
                                return@put
                            }
                            upload.option?.let { option ->
                                if (!listOf("ir", "ase", "meca", "tf", "gi").contains(option)) {
                                    call.response.status(HttpStatusCode.BadRequest)
                                    call.respond(mapOf(
                                        "error" to "Option invalide !"
                                    ))
                                    return@put
                                }
                            }
                            upload.year?.let { year ->
                                if (!listOf("1A", "2A", "3A", "other").contains(year)) {
                                    call.response.status(HttpStatusCode.BadRequest)
                                    call.respond(mapOf(
                                        "error" to "Année invalide !"
                                    ))
                                    return@put
                                }
                            }
                            Database.dbQuery {
                                Users.update({ Users.id eq selectedUser.id }) {
                                    it[Users.firstName] = upload.firstName ?: selectedUser.firstName!!
                                    it[Users.lastName] = upload.lastName ?: selectedUser.lastName!!
                                    it[Users.option] = upload.option ?: selectedUser.option!!
                                    it[Users.year] = upload.year ?: selectedUser.year!!
                                }
                            }
                            upload.expiration?.let { expiration ->
                                try {
                                    Database.dbQuery {
                                        if (selectedUser.cotisant != null) {
                                            Cotisants.update({ Cotisants.userId eq selectedUser.id }) {
                                                it[Cotisants.expiration] = expiration.toLocalDate().toString()
                                                it[Cotisants.updatedAt] = Clock.System.now().toString()
                                            }
                                        } else {
                                            Cotisants.insert {
                                                it[Cotisants.userId] = selectedUser.id
                                                it[Cotisants.expiration] = expiration.toLocalDate().toString()
                                                it[Cotisants.updatedAt] = Clock.System.now().toString()
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    call.response.status(HttpStatusCode.BadRequest)
                                    call.respond(mapOf(
                                        "error" to "Expiration invalide !"
                                    ))
                                    return@put
                                }
                            }
                            Database.dbQuery {
                                Users.customJoin().select { Users.id eq id }.mapUser(true).singleOrNull()
                            }?.let { call.respond(it) } ?: run {
                                call.response.status(HttpStatusCode.NotFound)
                                call.respond(mapOf("error" to "User not found"))
                            }
                        } ?: run {
                            call.response.status(HttpStatusCode.NotFound)
                            call.respond(mapOf("error" to "User not found"))
                        }
                    } ?: run {
                        call.response.status(HttpStatusCode.BadRequest)
                        call.respond(mapOf("error" to "Invalid user id"))
                    }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(mapOf("error" to "Not allowed to edit users"))
                }
            } ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
            }
        }
    }
}
