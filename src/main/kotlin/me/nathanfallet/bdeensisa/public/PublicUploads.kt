package me.nathanfallet.bdeensisa.public

import java.io.File
import kotlinx.datetime.*
import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import me.nathanfallet.bdeensisa.account.getUser
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.*
import org.jetbrains.exposed.sql.*

fun Route.publicUploads() {
    route("/uploads") {
        get ("/{name}") {
            call.parameters["name"]?.let { name ->
                val file = File("uploads/$name")
                if (file.exists()) {
                    call.respondFile(file)
                } else {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
            }
        }
    }
}
