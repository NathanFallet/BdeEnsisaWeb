package me.nathanfallet.bdeensisa.public

import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.Pages
import org.jetbrains.exposed.sql.*

fun Route.publicPages() {
    get {
        Database.dbQuery {
            Pages.select { Pages.isHome eq true }.firstOrNull()?.let {
                call.respond(FreeMarkerContent(
                    "public/pages.ftl",
                    mapOf(
                        "title" to it[Pages.title],
                        "content" to it[Pages.content]
                    )
                ))
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Aucune page d'accueil trouvée")))
            }
        }
    }
    get("/pages/{url}") {
        call.parameters["url"]?.let { url ->
            Database.dbQuery {
                Pages.select { Pages.url eq url }.firstOrNull()?.let {
                    call.respond(FreeMarkerContent(
                        "public/pages.ftl",
                        mapOf(
                            "title" to it[Pages.title],
                            "content" to it[Pages.content]
                        )
                    ))
                } ?: run {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                }
            }
        } ?: run {
            call.response.status(HttpStatusCode.NotFound)
            call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
        }
    }
}
