package me.nathanfallet.bdeensisa.admin

import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import kotlinx.datetime.*
import me.nathanfallet.bdeensisa.account.getUser
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.*
import org.jetbrains.exposed.sql.*

fun Route.adminUsers() {
    route("/users") {
        get {
            getUser()?.let { user ->
                if (user.hasPermission("admin.users.view")) {
                    val users = Database.dbQuery {
                        Users
                            .join(
                                Cotisants, JoinType.LEFT, Users.id, Cotisants.userId,
                                additionalConstraint = { Cotisants.expiration greater Clock.System.now().toString() }
                            )
                            .selectAll().map {
                                User(it, it.getOrNull(Cotisants.userId)?.run { Cotisant(it) })
                            }
                    }
                    call.respond(FreeMarkerContent("admin/users/list.ftl", mapOf(
                        "title" to "Utilisateurs",
                        "users" to users,
                        "menu" to MenuItems.fetchAdmin(user)
                    )))
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/users")
            }
        }
        get ("/{id}") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.users.view")) {
                    call.parameters["id"]?.let { id ->
                        Database.dbQuery {
                            Users
                                .join(
                                    Cotisants, JoinType.LEFT, Users.id, Cotisants.userId,
                                    additionalConstraint = { Cotisants.expiration greater Clock.System.now().toString() }
                                )
                                .select { Users.id eq id }.map {
                                    User(it, it.getOrNull(Cotisants.userId)?.run { Cotisant(it) })
                                }.singleOrNull()
                        }?.let { selectedUser ->
                            call.respond(FreeMarkerContent("admin/users/form.ftl", mapOf(
                                "title" to "Utilisateur",
                                "user" to selectedUser,
                                "menu" to MenuItems.fetchAdmin(user)
                            )))
                        } ?: run {
                            call.response.status(HttpStatusCode.NotFound)
                            call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                        }
                    } ?: run {
                        call.response.status(HttpStatusCode.NotFound)
                        call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                    }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/users")
            }
        }
        post ("/{id}") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.users.edit")) {
                    call.parameters["id"]?.let { id ->
                        Database.dbQuery {
                            Users
                                .join(
                                    Cotisants, JoinType.LEFT, Users.id, Cotisants.userId,
                                    additionalConstraint = { Cotisants.expiration greater Clock.System.now().toString() }
                                )
                                .select { Users.id eq id }.map {
                                    User(it, it.getOrNull(Cotisants.userId)?.run { Cotisant(it) })
                                }.singleOrNull()
                        }?.let { selectedUser ->
                            val params = call.receiveParameters()
                            val firstName = params["first_name"]
                            val lastName = params["last_name"]
                            val year = params["year"]
                            val option = params["option"]
                            val expiration = params["expiration"]?.toLocalDate()
                            if (firstName != null && lastName != null && year != null && option != null) {
                                Database.dbQuery {
                                    Users.update({ Users.id eq selectedUser.id }) {
                                        it[Users.firstName] = firstName
                                        it[Users.lastName] = lastName
                                        it[Users.year] = year
                                        it[Users.option] = option
                                    }
                                }
                                call.respondRedirect("/admin/users/$id")
                            } else if (expiration != null) {
                                Database.dbQuery {
                                    if (selectedUser.cotisant != null) {
                                        Cotisants.update({ Cotisants.userId eq selectedUser.id }) {
                                            it[Cotisants.expiration] = expiration.toString()
                                            it[Cotisants.updatedAt] = Clock.System.now().toString()
                                        }
                                    } else {
                                        Cotisants.insert {
                                            it[Cotisants.userId] = selectedUser.id
                                            it[Cotisants.expiration] = expiration.toString()
                                            it[Cotisants.updatedAt] = Clock.System.now().toString()
                                        }
                                    }
                                }
                                call.respondRedirect("/admin/users/$id")
                            } else {
                                call.response.status(HttpStatusCode.BadRequest)
                                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                            }
                        } ?: run {
                            call.response.status(HttpStatusCode.NotFound)
                            call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                        }
                    } ?: run {
                        call.response.status(HttpStatusCode.NotFound)
                        call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                    }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/users")
            }
        }
    }
}
