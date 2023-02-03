package me.nathanfallet.bdeensisa.public

import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.*
import org.jetbrains.exposed.sql.*

fun Route.publicPages() {
    get {
        val page = Database.dbQuery {
            Pages.select { Pages.home eq true }.map { Page(it) }.singleOrNull()
        } ?: run {
            call.response.status(HttpStatusCode.NotFound)
            call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Aucune page d'accueil trouvée")))
            return@get
        }
        call.respond(FreeMarkerContent(
            "public/pages/view.ftl",
            mapOf(
                "title" to page.title,
                "markdown" to page.markdown,
                "menu" to MenuItems.fetch()
            )
        ))
    }
    get("/pages/{url}") {
        val page = call.parameters["url"]?.let { url ->
            Database.dbQuery {
                Pages.select { Pages.url eq url }.map { Page(it) }.singleOrNull()
            }
        } ?: run {
            call.response.status(HttpStatusCode.NotFound)
            call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
            return@get
        }
        call.respond(FreeMarkerContent(
            "public/pages/view.ftl",
            mapOf(
                "title" to page.title,
                "markdown" to page.markdown,
                "menu" to MenuItems.fetch()
            )
        ))
    }
}
