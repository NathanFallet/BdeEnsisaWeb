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
            val user = getUser()
            if (user == null) {
                call.respondRedirect("/account/login?redirect=/admin/users")
                return@get
            }
            if (!user.hasPermission("admin.users.view")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val users = Database.dbQuery {
                Users.customJoin().selectAll().mapUser(false)
            }
            call.respond(FreeMarkerContent("admin/users/list.ftl", mapOf(
                "title" to "Utilisateurs",
                "users" to users,
                "menu" to MenuItems.fetchAdmin(user)
            )))
        }
        get ("/{id}") {
            val user = getUser()
            if (user == null) {
                call.respondRedirect("/account/login?redirect=/admin/users")
                return@get
            }
            if (!user.hasPermission("admin.users.view")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val selectedUser = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Users.customJoin().select { Users.id eq id }.mapUser(false).singleOrNull()
                }
            }
            if (selectedUser == null) {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            call.respond(FreeMarkerContent("admin/users/form.ftl", mapOf(
                "title" to "Utilisateur",
                "user" to selectedUser,
                "menu" to MenuItems.fetchAdmin(user)
            )))
        }
        post ("/{id}") {
            val user = getUser()
            if (user == null) {
                call.respondRedirect("/account/login?redirect=/admin/users")
                return@post
            }
            if (!user.hasPermission("admin.users.edit")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@post
            }
            val selectedUser = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Users.customJoin().select { Users.id eq id }.mapUser(false).singleOrNull()
                }
            }
            if (selectedUser == null) {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@post
            }
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
                call.respondRedirect("/admin/users/${selectedUser.id}")
                return@post
            }
            if (expiration != null) {
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
                call.respondRedirect("/admin/users/${selectedUser.id}")
                return@post
            }
            call.response.status(HttpStatusCode.BadRequest)
            call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
        }
    }
}
