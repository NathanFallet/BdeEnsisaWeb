package me.nathanfallet.bdeensisa.plugins

import com.google.auth.oauth2.GoogleCredentials
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.server.application.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.coroutines.*
import java.io.FileInputStream
import me.nathanfallet.bdeensisa.models.NotificationsTokens
import me.nathanfallet.bdeensisa.database.Database
import org.jetbrains.exposed.sql.*

object Notifications {

    private val credentials = GoogleCredentials
        .fromStream(FileInputStream("firebase-adminsdk.json"))
        .createScoped("https://www.googleapis.com/auth/firebase.messaging")

    @OptIn(ExperimentalSerializationApi::class)
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }
    }

    private suspend fun sendNotification(notification: NotificationPayload) {
        val token = credentials.refreshAccessToken()
        httpClient.post("https://fcm.googleapis.com/v1/projects/bde-ensisa/messages:send") {
            header("Authorization", "Bearer ${token.tokenValue}")
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "message" to notification
            ))
        }
    }

    fun sendNotificationToUser(userId: String, notification: Notification) {
        CoroutineScope(Job()).launch {
            Database.dbQuery {
                NotificationsTokens
                    .select { NotificationsTokens.userId eq userId }
                    .map { it[NotificationsTokens.token] }
            }.forEach { token ->
                sendNotification(NotificationPayload(
                    token = token,
                    notification = notification
                ))
            }
        }
    }

    fun sendNotificationToTopic(topic: String, notification: Notification) {
        CoroutineScope(Job()).launch {
            sendNotification(NotificationPayload(
                topic = topic,
                notification = notification
            ))
        }
    }

}

@Serializable
data class NotificationPayload(
    val token: String? = null,
    val topic: String? = null,
    val notification: Notification
)

@Serializable
data class Notification(
    val title: String,
    val body: String
)
