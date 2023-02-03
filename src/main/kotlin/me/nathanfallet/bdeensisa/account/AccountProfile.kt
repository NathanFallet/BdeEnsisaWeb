package me.nathanfallet.bdeensisa.account

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
import org.jetbrains.exposed.sql.*

fun Route.accountProfile() {
    get ("/profile") {
        val user = getUser() ?: run {
            call.respondRedirect("/account/login")
            return@get
        }
        val cotisant = Database.dbQuery {
            Cotisants.select {
                Cotisants.userId eq user.id and (Cotisants.expiration greater Clock.System.now().toString())
            }.map { Cotisant(it) }.singleOrNull()
        }
        call.respond(FreeMarkerContent(
            "account/profile.ftl",
            mapOf(
                "title" to "Mon profil",
                "user" to user,
                "cotisant" to cotisant,
                "menu" to MenuItems.fetch()
            )
        ))
    }
}
