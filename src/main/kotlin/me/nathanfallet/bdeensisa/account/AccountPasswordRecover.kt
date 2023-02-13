package me.nathanfallet.bdeensisa.account

import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.server.application.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.http.HttpStatusCode
import kotlinx.datetime.*
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.*
import me.nathanfallet.bdeensisa.plugins.Emails
import org.jetbrains.exposed.sql.*

fun Route.accountPasswordRecovery() {
    get("/password-recovery") {
        call.respond(FreeMarkerContent(
            "account/password-recovery.ftl",
            mapOf(
                "title" to "Mot de passe oublié"
            )
        ))
    }
    post("/password-recovery") {
        val params = call.receiveParameters()
        val email = params["email"]
        if (email == null) {
            call.respond(FreeMarkerContent(
                "account/password-recovery.ftl",
                mapOf(
                    "title" to "Mot de passe oublié",
                    "error" to "Remplissez tous les champs !",
                )
            ))
            return@post
        }
        val user = Database.dbQuery {
            Users.select { Users.email eq email }.map { User(it) }.singleOrNull()
        } ?: run {
            call.respond(FreeMarkerContent(
                "account/password-recovery.ftl",
                mapOf(
                    "title" to "Mot de passe oublié",
                    "error" to "Ce compte n'existe pas !"
                )
            ))
            return@post
        }
        val expiration = Clock.System.now().plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        val code = Database.dbQuery {
            val code = PasswordRequests.generateCode()
            PasswordRequests.insert {
                it[PasswordRequests.userId] = user.id
                it[PasswordRequests.code] = code
                it[PasswordRequests.expiration] = expiration.toString()
            }
            code
        }
        Emails.sendEmail(
            user.email!!,
            "Mot de passe oublié",
            "<p>Afin de cr&eacute;er un nouveau mot de passe, cliquez sur le lien suivant, valide pendant 24h :</p>" +
            "<p><a href='https://bdensisa.org/account/password-recovery/$code'>https://bdensisa.org/account/password-recovery/$code</a></p>" +
            "<p>- L'&eacute;quipe du BDE de l'ENSISA</p>"
        )
        call.respond(FreeMarkerContent(
            "account/password-recovery.ftl",
            mapOf(
                "title" to "Mot de passe oublié",
                "success" to "Un email de récupération vous a été envoyé !",
                "redirect" to call.request.queryParameters["redirect"]
            )
        ))
    }
    get("/password-recovery/{code}") {
        val request = call.parameters["code"]?.let { code ->
            Database.dbQuery {
                PasswordRequests.select { PasswordRequests.code eq code }.map { PasswordRequest(it) }.singleOrNull()
            }
        } ?: run {
            call.response.status(HttpStatusCode.NotFound)
            call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
            return@get
        }
        call.respond(FreeMarkerContent(
            "account/password-recovery.ftl",
            mapOf(
                "title" to "Mot de passe oublié",
                "request" to request
            )
        ))
    }
    post("/password-recovery/{code}") {
        val request = call.parameters["code"]?.let { code ->
            Database.dbQuery {
                PasswordRequests.select { PasswordRequests.code eq code }.map { PasswordRequest(it) }.singleOrNull()
            }
        } ?: run {
            call.response.status(HttpStatusCode.NotFound)
            call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
            return@post
        }
        val params = call.receiveParameters()
        val password = params["password"]
        val password2 = params["password2"]
        if (
            password == null ||
            password2 == null
        ) {
            call.respond(FreeMarkerContent(
                "account/password-recovery.ftl",
                mapOf(
                    "title" to "Mot de passe oublié",
                    "request" to request,
                    "error" to "Remplissez tous les champs !"
                )
            ))
            return@post
        }
        if (password != password2) {
            call.respond(FreeMarkerContent(
                "account/password-recovery.ftl",
                mapOf(
                    "title" to "Mot de passe oublié",
                    "request" to request,
                    "error" to "Les mots de passe ne correspondent pas !"
                )
            ))
            return@post
        }
        Database.dbQuery {
            PasswordRequests.deleteWhere {
                Op.build { PasswordRequests.code eq request.code }
            }
            Users.update({ Users.id eq request.userId }) {
                it[Users.password] = BCrypt.withDefaults().hashToString(12, password.toCharArray())
            }
        }
        call.respondRedirect("/account/login")
    }
}
