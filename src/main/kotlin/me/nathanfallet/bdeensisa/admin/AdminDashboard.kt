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

fun Route.adminDashboard() {
    get {
        getUser()?.let { user ->
            if (user.hasPermission("admin.dashboard")) {
                val usersCount = Database.dbQuery {
                    Users.selectAll().count()
                }
                val cotisantsCount = Database.dbQuery {
                    Cotisants.select { Cotisants.expiration greater Clock.System.now().toString() }.count()
                }
                val pagesCount = Database.dbQuery {
                    Pages.selectAll().count()
                }
                call.respond(FreeMarkerContent("admin/dashboard.ftl", mapOf(
                    "title" to "Tableau de bord",
                    "menu" to MenuItems.fetchAdmin(user),
                    "counts" to mapOf(
                        "users" to usersCount,
                        "cotisants" to cotisantsCount,
                        "pages" to pagesCount
                    )
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
