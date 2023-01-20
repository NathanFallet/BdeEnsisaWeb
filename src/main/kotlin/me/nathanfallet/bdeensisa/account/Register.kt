package me.nathanfallet.bdeensisa.account

import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.server.application.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.datetime.*
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.User
import me.nathanfallet.bdeensisa.models.Users
import org.jetbrains.exposed.sql.*

fun Route.register() {
    route("/register") {
        get {
            call.respond(
                    FreeMarkerContent(
                        "account/register.ftl",
                        mapOf(
                            "title" to "Inscription",
                            "redirect" to call.request.queryParameters["redirect"]
                        )
                    )
            )
        }
        post {
            val params = call.receiveParameters()
            val firstName = params["first_name"]
            val lastName = params["last_name"]
            val email = params["email"]
            val option = params["option"]
            val year = params["year"]
            val password = params["password"]
            val password2 = params["password2"]
            if (firstName == null ||
                            lastName == null ||
                            email == null ||
                            option == null ||
                            year == null ||
                            password == null ||
                            password2 == null
            ) {
                call.respond(
                        FreeMarkerContent(
                                "account/register.ftl",
                                mapOf(
                                    "title" to "Inscription",
                                    "error" to "Remplissez tous les champs !",
                                    "redirect" to call.request.queryParameters["redirect"]
                                )
                        )
                )
                return@post
            }
            if (!listOf("ir", "ase", "meca", "tf", "gi").contains(option)) {
                call.respond(
                        FreeMarkerContent(
                                "account/register.ftl",
                                mapOf(
                                    "title" to "Inscription",
                                    "error" to "Option invalide !",
                                    "redirect" to call.request.queryParameters["redirect"]
                                )
                        )
                )
                return@post
            }
            if (!listOf("1A", "2A", "3A", "other").contains(year)) {
                call.respond(
                        FreeMarkerContent(
                                "account/register.ftl",
                                mapOf(
                                    "title" to "Inscription",
                                    "error" to "Année invalide !",
                                    "redirect" to call.request.queryParameters["redirect"]
                                )
                        )
                )
                return@post
            }
            if (password != password2) {
                call.respond(
                        FreeMarkerContent(
                                "account/register.ftl",
                                mapOf(
                                    "title" to "Inscription",
                                    "error" to "Les mots de passe ne correspondent pas !",
                                    "redirect" to call.request.queryParameters["redirect"]
                                )
                        )
                )
                return@post
            }
            Database.dbQuery {
                Users.select { Users.email eq email }.map { User(it) }.singleOrNull()
            }
                    ?.let {
                        call.respond(
                                FreeMarkerContent(
                                        "account/register.ftl",
                                        mapOf(
                                            "title" to "Inscription",
                                            "error" to "Email déjà utilisé !",
                                            "redirect" to call.request.queryParameters["redirect"]
                                        )
                                )
                        )
                        return@post
                    }

            Database.dbQuery {
                Users.insert {
                    it[Users.id] = Users.generateId()
                    it[Users.firstName] = firstName
                    it[Users.lastName] = lastName
                    it[Users.email] = email
                    it[Users.option] = option
                    it[Users.year] = year
                    it[Users.password] =
                            BCrypt.withDefaults().hashToString(12, password.toCharArray())
                }
            }
                    .resultedValues
                    ?.singleOrNull()
                    ?.let {
                        call.sessions.set(AccountSession(it[Users.id]))
                        call.respondRedirect(call.request.queryParameters["redirect"] ?: "/account")
                    }
                    ?: run {
                        call.respond(
                                FreeMarkerContent(
                                        "account/register.ftl",
                                        mapOf(
                                            "title" to "Inscription",
                                            "error" to "Erreur lors de l'inscription !",
                                            "redirect" to call.request.queryParameters["redirect"]
                                        )
                                )
                        )
                    }
        }
    }
}
