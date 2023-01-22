package me.nathanfallet.bdeensisa.admin

import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import me.nathanfallet.bdeensisa.account.getUser
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.User
import me.nathanfallet.bdeensisa.models.MenuItems
import org.jetbrains.exposed.sql.*

fun Route.adminDashboard() {
    get {
        getUser()?.let { user ->
            if (user.hasPermission("admin.dashboard")) {
                call.respond(FreeMarkerContent("admin/dashboard.ftl", mapOf(
                    "title" to "Tableau de bord",
                    "menu" to MenuItems.fetchAdmin(user)
                )))
            } else {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
            }
        } ?: run {
            call.respondRedirect("/account/login?redirect=/admin")
        }
    }
}
