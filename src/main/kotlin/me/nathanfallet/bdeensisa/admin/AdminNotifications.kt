package me.nathanfallet.bdeensisa.admin

import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.http.content.*
import io.ktor.http.HttpStatusCode
import me.nathanfallet.bdeensisa.account.getUser
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.*
import me.nathanfallet.bdeensisa.plugins.Notifications
import me.nathanfallet.bdeensisa.plugins.Notification
import org.jetbrains.exposed.sql.*

fun Route.adminNotifications() {
    route("/notifications") {
        get {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/notifications")
                return@get
            }
            if (!user.hasPermission("admin.notifications")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            call.respond(FreeMarkerContent("admin/notifications/send.ftl", mapOf(
                "title" to "Notifications",
                "menu" to MenuItems.fetchAdmin(user)
            )))
        }
        post {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/notifications")
                return@post
            }
            if (!user.hasPermission("admin.notifications")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@post
            }
            val params = call.receiveParameters()
            val title = params["title"]?.trim()
            val body = params["body"]
            val topic = params["topic"] ?: "broadcast"
            if (title == null || body == null) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                return@post
            }
            Notifications.sendNotificationToTopic(
                topic,
                Notification(title, body)
            )
            call.respond(FreeMarkerContent("admin/notifications/send.ftl", mapOf(
                "title" to "Notifications",
                "menu" to MenuItems.fetchAdmin(user),
                "success" to true
            )))
        }
    }
}
