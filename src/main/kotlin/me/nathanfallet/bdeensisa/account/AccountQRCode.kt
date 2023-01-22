package me.nathanfallet.bdeensisa.account

import io.github.g0dkar.qrcode.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.http.HttpStatusCode
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.User
import me.nathanfallet.bdeensisa.models.Users
import org.jetbrains.exposed.sql.*

fun Route.accountQRCode() {
    get ("/qrcode") {
        getUser()?.let { user ->
            QRCode("users/${user.id}").render().getBytes("PNG").let { bytes ->
                call.response.header("Content-Type", "image/png")
                call.respond(bytes)
            }
        } ?: run {
            call.response.status(HttpStatusCode.NotFound)
            call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
        }
    }
}
