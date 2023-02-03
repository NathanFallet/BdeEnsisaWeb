package me.nathanfallet.bdeensisa.admin

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlinx.datetime.*
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
import org.jetbrains.exposed.sql.*

fun Route.adminUploads() {
    route("/uploads") {
        get {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/uploads")
                return@get
            }
            if (!user.hasPermission("admin.uploads.view")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@get
            }
            val uploadsFolder = Paths.get("uploads")
            if (!Files.exists(uploadsFolder)) {
                Files.createDirectory(uploadsFolder)
            }
            val uploads = Files.walk(uploadsFolder)
                .filter { Files.isRegularFile(it) }
                .map { it.toFile() }.toList()
            call.respond(FreeMarkerContent("admin/uploads/list.ftl", mapOf(
                "title" to "Téléchargements",
                "uploads" to uploads,
                "menu" to MenuItems.fetchAdmin(user)
            )))
        }
        post {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/admin/uploads")
                return@post
            }
            if (!user.hasPermission("admin.uploads.new")) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                return@post
            }
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val name = part.originalFileName!!
                    val file = File("uploads/$name")

                    part.streamProvider().use { its ->
                        file.outputStream().buffered().use {
                            its.copyTo(it)
                        }
                    }
                }
                part.dispose()
            }
        }
    }
}
