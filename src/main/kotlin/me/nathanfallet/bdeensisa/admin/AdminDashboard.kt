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
        val user = getUser() ?: run {
            call.respondRedirect("/account/login?redirect=/admin")
            return@get
        }
        if (!user.hasPermission("admin.dashboard")) {
            call.response.status(HttpStatusCode.Forbidden)
            call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
            return@get
        }
        val cotisantsCount = Database.dbQuery {
            Cotisants.select { Cotisants.expiration greater Clock.System.now().toString() }.count()
        }
        val lastCotisants = Database.dbQuery {
            Cotisants
                .join(Users, JoinType.INNER, Cotisants.userId, Users.id)
                .select { Cotisants.expiration greater Clock.System.now().toString() }
                .orderBy(Cotisants.updatedAt, SortOrder.DESC)
                .limit(5)
                .map { Cotisant(it, User(it)) }
        }
        val topicsCount = Database.dbQuery {
            Topics.select { Topics.validated eq true }.count()
        }
        val questionsCount = Database.dbQuery {
            Questions.selectAll().count()
        }
        val allEvents = Database.dbQuery {
            Events.selectAll().map { Event(it) }
        }
        call.respond(FreeMarkerContent("admin/dashboard.ftl", mapOf(
            "title" to "Tableau de bord",
            "menu" to MenuItems.fetchAdmin(user),
            "counts" to mapOf(
                "cotisants" to cotisantsCount,
                "topics" to topicsCount,
                "questions" to questionsCount,
                "events" to allEvents.size
            ),
            "tables" to mapOf(
                "cotisants" to lastCotisants
            ),
            "calendar" to allEvents
        )))
    }
}
