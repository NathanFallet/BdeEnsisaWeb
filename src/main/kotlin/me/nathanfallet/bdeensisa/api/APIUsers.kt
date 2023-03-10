package me.nathanfallet.bdeensisa.api

import io.ktor.http.content.*
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
import me.nathanfallet.bdeensisa.plugins.Notification
import org.jetbrains.exposed.sql.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

fun Route.apiUsers() {
    route("/users") {
        get("/me") {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@get
            }
            call.respond(user)
        }
        put("/me") {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@put
            }
            val upload = try {
                call.receive<UserUpload>()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid body"))
                return@put
            }
            upload.option?.let { option ->
                if (!listOf("ir", "ase", "meca", "tf", "gi").contains(option)) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf(
                        "error" to "Option invalide !"
                    ))
                    return@put
                }
            }
            upload.year?.let { year ->
                if (!listOf("CPB", "1A", "2A", "3A", "other").contains(year)) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf(
                        "error" to "Ann??e invalide !"
                    ))
                    return@put
                }
            }
            Database.dbQuery {
                Users.update({ Users.id eq user.id }) {
                    it[Users.firstName] = upload.firstName ?: user.firstName!!
                    it[Users.lastName] = upload.lastName ?: user.lastName!!
                    it[Users.option] = upload.option ?: user.option!!
                    it[Users.year] = upload.year ?: user.year!!
                }
            }
            val newUser = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@put
            }
            call.respond(newUser)
        }
        get {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@get
            }
            if (!user.hasPermission("admin.users.view")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(mapOf("error" to "Not allowed to view users"))
                return@get
            }
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
            val search = call.request.queryParameters["search"]
            val users = Database.dbQuery {
                val join = Users.customJoin()
                val select = search?.let {
                    join.select {
                        Users.firstName like "%$it%" or
                        (Users.lastName like "%$it%") or
                        (Users.email like "%$it%")
                    }
                } ?: join.selectAll()
                select.limit(limit, offset).mapUser(false)
            }
            call.respond(users)
        }
        get("/{id}") {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@get
            }
            if (!user.hasPermission("admin.users.view")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(mapOf("error" to "Not allowed to view users"))
                return@get
            }
            val selectedUser = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Users.customJoin().select { Users.id eq id }.mapUser(true).singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(mapOf("error" to "User not found"))
                return@get
            }
            call.respond(selectedUser)
        }
        put("/{id}") {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@put
            }
            if (!user.hasPermission("admin.users.edit")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(mapOf("error" to "Not allowed to edit users"))
                return@put
            }
            val selectedUser = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Users.customJoin().select { Users.id eq id }.mapUser(false).singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(mapOf("error" to "User not found"))
                return@put
            }
            val upload = try {
                call.receive<UserUpload>()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid body"))
                return@put
            }
            upload.option?.let { option ->
                if (!listOf("ir", "ase", "meca", "tf", "gi").contains(option)) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf(
                        "error" to "Option invalide !"
                    ))
                    return@put
                }
            }
            upload.year?.let { year ->
                if (!listOf("CPB", "1A", "2A", "3A", "other").contains(year)) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf(
                        "error" to "Ann??e invalide !"
                    ))
                    return@put
                }
            }
            Database.dbQuery {
                Users.update({ Users.id eq selectedUser.id }) {
                    it[Users.firstName] = upload.firstName?.trim() ?: selectedUser.firstName!!
                    it[Users.lastName] = upload.lastName?.trim() ?: selectedUser.lastName!!
                    it[Users.option] = upload.option ?: selectedUser.option!!
                    it[Users.year] = upload.year ?: selectedUser.year!!
                }
            }
            upload.expiration?.let { expiration ->
                try {
                    Database.dbQuery {
                        try {
                            Cotisants.insert {
                                it[Cotisants.userId] = selectedUser.id
                                it[Cotisants.expiration] = expiration.toLocalDate().toString()
                                it[Cotisants.updatedAt] = Clock.System.now().toString()
                            }
                        } catch (e: Exception) {
                            Cotisants.update({ Cotisants.userId eq selectedUser.id }) {
                                it[Cotisants.expiration] = expiration.toLocalDate().toString()
                                it[Cotisants.updatedAt] = Clock.System.now().toString()
                            }
                        }
                        Notifications.sendNotificationToUser(
                            selectedUser.id,
                            Notification(
                                "Statut de cotisant mis ?? jour !",
                                "Votre statut de cotisant a ??t?? mis ?? jour"
                            )
                        )
                    }
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf(
                        "error" to "Expiration invalide !"
                    ))
                    return@put
                }
            }
            val newUser = Database.dbQuery {
                Users.customJoin().select { Users.id eq selectedUser.id }.mapUser(true).singleOrNull()
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(mapOf("error" to "User not found"))
                return@put
            }
            call.respond(newUser)
        }
        get("/{id}/picture") {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@get
            }
            if (!user.hasPermission("admin.users.view") && user.id != call.parameters["id"]) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(mapOf("error" to "Not allowed to view user pictures"))
                return@get
            }
            val file = File("uploads/users/${call.parameters["id"]}/picture.jpg")
            if (!file.exists()) {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(mapOf("error" to "File not found"))
                return@get
            }
            call.respondFile(file)
        }
        post("/{id}/picture") {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@post
            }
            if (!user.hasPermission("admin.users.edit") && user.id != call.parameters["id"]) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(mapOf("error" to "Not allowed to edit user pictures"))
                return@post
            }
            val uploadsFolder = Paths.get("uploads/users")
            if (!Files.exists(uploadsFolder)) {
                Files.createDirectory(uploadsFolder)
            }
            val userFolder = Paths.get("uploads/users/${call.parameters["id"]}")
            if (!Files.exists(userFolder)) {
                Files.createDirectory(userFolder)
            }
            call.receiveStream().use { input ->
                val file = File("uploads/users/${call.parameters["id"]}/picture.jpg")
                withContext(Dispatchers.IO) {
                    file.outputStream().buffered().use {
                        input.copyTo(it)
                    }
                }
            }
            call.response.status(HttpStatusCode.Created)
        }
    }
}
