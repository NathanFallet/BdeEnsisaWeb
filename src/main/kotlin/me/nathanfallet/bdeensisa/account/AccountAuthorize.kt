package me.nathanfallet.bdeensisa.account

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.datetime.*
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.*
import org.jetbrains.exposed.sql.*

fun Route.accountAuthorize() {
    val redirect = this.environment!!.config.property("mobile.client.redirect").getString()

    get("/authorize") {
        getUser()?.let { user ->
            Database.dbQuery {
                val expiration = Clock.System.now()
                    .plus(1, DateTimeUnit.HOUR, TimeZone.currentSystemDefault())
                LoginAuthorizes.insert {
                    it[LoginAuthorizes.code] = LoginAuthorizes.generateCode()
                    it[LoginAuthorizes.user] = user.id
                    it[LoginAuthorizes.expiration] = expiration.toString()
                }.resultedValues?.map { LoginAuthorize(it) }?.singleOrNull()
            }
        }?.let { auth ->
            val separator = if (redirect.contains("?")) "&" else "?"
            val url = "$redirect${separator}code=${auth.code}".replace("&", "&amp;")
            call.respond(FreeMarkerContent(
                "account/redirect.ftl",
                mapOf(
                    "title" to "Redirection",
                    "redirectUrl" to url
                )
            ))
        } ?: run {
            call.respondRedirect("/account/login?redirect=/account/authorize")
        }
    }
}
