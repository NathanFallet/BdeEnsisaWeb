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

fun Route.accountRegister() {
    get("/register") {
        call.respond(FreeMarkerContent(
            "account/register.ftl",
            mapOf(
                "title" to "Inscription",
                "redirect" to call.request.queryParameters["redirect"]
            )
        ))
    }
    post("/register") {
        val params = call.receiveParameters()
        val raw_email = params["email"]
        if (raw_email == null) {
            call.respond(FreeMarkerContent(
                "account/register.ftl",
                mapOf(
                    "title" to "Inscription",
                    "error" to "Remplissez tous les champs !",
                    "redirect" to call.request.queryParameters["redirect"]
                )
            ))
            return@post
        }
        if (!Regex("[a-z\\-]+\\.[a-z\\-]+").matches(raw_email)) {
            call.respond(FreeMarkerContent(
                "account/register.ftl",
                mapOf(
                    "title" to "Inscription",
                    "error" to "Email invalide !",
                    "redirect" to call.request.queryParameters["redirect"]
                )
            ))
            return@post
        }
        val email = "$raw_email@uha.fr"
        Database.dbQuery {
            Users.select { Users.email eq email }.map { User(it) }.singleOrNull() ?:
            RegistrationRequests.select { RegistrationRequests.email eq email }.map { RegistrationRequest(it) }.singleOrNull()
        }?.let {
            call.respond(FreeMarkerContent(
                "account/register.ftl",
                mapOf(
                    "title" to "Inscription",
                    "error" to "Email déjà utilisé !",
                    "redirect" to call.request.queryParameters["redirect"]
                )
            ))
            return@post
        }
        val expiration = Clock.System.now().plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        val code = Database.dbQuery {
            val code = RegistrationRequests.generateCode()
            RegistrationRequests.insert {
                it[RegistrationRequests.email] = email
                it[RegistrationRequests.code] = code
                it[RegistrationRequests.expiration] = expiration.toString()
            }
            code
        }
        Emails.sendEmail(
            email,
            "Inscription sur le site du BDE de l'ENSISA",
            "<p>Bienvenue &agrave; bord !<br/>" +
            "Finalisez votre inscription en cliquant sur le lien suivant, valide pendant 24h :</p>" +
            "<p><a href='https://bde.ensisa.info/account/register/$code'>https://bde.ensisa.info/account/register/$code</a></p>" +
            "<p>- L'&eacute;quipe du BDE de l'ENSISA</p>"
        )
        call.respond(FreeMarkerContent(
            "account/register.ftl",
            mapOf(
                "title" to "Inscription",
                "success" to "Un email d'inscription vous a été envoyé !",
                "redirect" to call.request.queryParameters["redirect"]
            )
        ))
    }
    get("/register/{code}") {
        call.parameters["code"]?.let { code ->
            Database.dbQuery {
                RegistrationRequests.select { RegistrationRequests.code eq code }.map { RegistrationRequest(it) }.singleOrNull()
            }?.let { request ->
                call.respond(FreeMarkerContent(
                    "account/register.ftl",
                    mapOf(
                        "title" to "Inscription",
                        "request" to request,
                        "redirect" to call.request.queryParameters["redirect"]
                    )
                ))
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
            }
        } ?: run {
            call.response.status(HttpStatusCode.NotFound)
            call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
        }
    }
    post("/register/{code}") {
        call.parameters["code"]?.let { code ->
            Database.dbQuery {
                RegistrationRequests.select { RegistrationRequests.code eq code }.map { RegistrationRequest(it) }.singleOrNull()
            }?.let { request ->
                val params = call.receiveParameters()
                val firstName = params["first_name"]
                val lastName = params["last_name"]
                val option = params["option"]
                val year = params["year"]
                val password = params["password"]
                val password2 = params["password2"]
                if (
                    firstName == null ||
                    lastName == null ||
                    option == null ||
                    year == null ||
                    password == null ||
                    password2 == null
                ) {
                    call.respond(FreeMarkerContent(
                        "account/register.ftl",
                        mapOf(
                            "title" to "Inscription",
                            "error" to "Remplissez tous les champs !",
                            "redirect" to call.request.queryParameters["redirect"]
                        )
                    ))
                    return@post
                }
                if (!listOf("ir", "ase", "meca", "tf", "gi").contains(option)) {
                    call.respond(FreeMarkerContent(
                        "account/register.ftl",
                        mapOf(
                            "title" to "Inscription",
                            "error" to "Option invalide !",
                            "redirect" to call.request.queryParameters["redirect"]
                        )
                    ))
                    return@post
                }
                if (!listOf("1A", "2A", "3A", "other").contains(year)) {
                    call.respond(FreeMarkerContent(
                        "account/register.ftl",
                        mapOf(
                            "title" to "Inscription",
                            "error" to "Année invalide !",
                            "redirect" to call.request.queryParameters["redirect"]
                        )
                    ))
                    return@post
                }
                if (password != password2) {
                    call.respond(FreeMarkerContent(
                        "account/register.ftl",
                        mapOf(
                            "title" to "Inscription",
                            "error" to "Les mots de passe ne correspondent pas !",
                            "redirect" to call.request.queryParameters["redirect"]
                        )
                    ))
                    return@post
                }

                Database.dbQuery {
                    RegistrationRequests.deleteWhere {
                        Op.build { RegistrationRequests.code eq request.code }
                    }
                    Users.insert {
                        it[Users.id] = Users.generateId()
                        it[Users.firstName] = firstName
                        it[Users.lastName] = lastName
                        it[Users.email] = request.email
                        it[Users.option] = option
                        it[Users.year] = year
                        it[Users.password] = BCrypt.withDefaults().hashToString(12, password.toCharArray())
                    }
                }.resultedValues?.singleOrNull()?.let {
                    call.sessions.set(AccountSession(it[Users.id]))
                    call.respondRedirect(
                        call.request.queryParameters["redirect"] ?: "/account/profile"
                    )
                } ?: run {
                    call.respond(FreeMarkerContent(
                        "account/register.ftl",
                        mapOf(
                            "title" to "Inscription",
                            "error" to "Erreur lors de l'inscription !",
                            "redirect" to call.request.queryParameters["redirect"]
                        )
                    ))
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
            }
        } ?: run {
            call.response.status(HttpStatusCode.NotFound)
            call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
        }
    }
}
