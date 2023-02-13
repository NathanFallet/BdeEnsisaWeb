package me.nathanfallet.bdeensisa.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.*
import me.nathanfallet.bdeensisa.plugins.Notifications
import me.nathanfallet.bdeensisa.plugins.NotificationPayload
import org.jetbrains.exposed.sql.*

fun Route.apiNotifications() {
    route("/notifications") {
        post {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@post
            }
            if (!user.hasPermission("admin.notifications")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(mapOf("error" to "Not allowed to send notifications"))
                return@post
            }
            val payload = try {
                call.receive<NotificationPayload>()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Missing data"))
                return@post
            }
            Notifications.sendNotificationFromPayload(payload)
            call.response.status(HttpStatusCode.Created)
        }
        post("/tokens") {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@post
            }
            val token = try {
                call.receive<NotificationsTokenUpload>()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Missing data"))
                return@post
            }
            val expiration = Clock.System.now().plus(1, DateTimeUnit.YEAR, TimeZone.currentSystemDefault())
            Database.dbQuery {
                try {
                    NotificationsTokens.insert {
                        it[NotificationsTokens.token] = token.token
                        it[NotificationsTokens.userId] = user.id
                        it[NotificationsTokens.expiration] = expiration.toString()
                    }
                } catch (e: Exception) {
                    NotificationsTokens.update({
                        NotificationsTokens.token eq token.token
                    }) {
                        it[NotificationsTokens.userId] = user.id
                        it[NotificationsTokens.expiration] = expiration.toString()
                    }
                }
            }
            call.response.status(HttpStatusCode.Created)
        }
    }
}
