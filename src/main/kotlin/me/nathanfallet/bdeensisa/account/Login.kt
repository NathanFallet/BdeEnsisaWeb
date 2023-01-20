package me.nathanfallet.bdeensisa.account

import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.server.application.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.User
import me.nathanfallet.bdeensisa.models.Users
import org.jetbrains.exposed.sql.*

fun Route.login() {
    route("/login") {
        get {
            call.respond(
                    FreeMarkerContent(
                            "account/login.ftl",
                            mapOf(
                                "title" to "Connexion",
                                "redirect" to call.request.queryParameters["redirect"]
                            )
                    )
            )
        }
        post {
            val params = call.receiveParameters()
            val email = params["email"]
            val password = params["password"]
            if (email == null || password == null) {
                call.respond(
                        FreeMarkerContent(
                                "account/login.ftl",
                                mapOf(
                                    "title" to "Connexion",
                                    "error" to "Email ou mot de passe invalide !",
                                    "redirect" to call.request.queryParameters["redirect"]
                                )
                        )
                )
                return@post
            }
            Database.dbQuery {
                Users.select { Users.email eq email }
                        .map { User(it) }
                        .singleOrNull()
            }
                    ?.takeIf {
                        BCrypt.verifyer().verify(password.toCharArray(), it.password).verified
                    }
                    ?.let {
                        call.sessions.set(AccountSession(it.id))
                        call.respondRedirect(call.request.queryParameters["redirect"] ?: "/account")
                    }
                    ?: run {
                        call.respond(
                                FreeMarkerContent(
                                        "account/login.ftl",
                                        mapOf(
                                            "title" to "Connexion",
                                            "error" to "Email ou mot de passe invalide !",
                                            "redirect" to call.request.queryParameters["redirect"]
                                        )
                                )
                        )
                    }
        }
    }
}
