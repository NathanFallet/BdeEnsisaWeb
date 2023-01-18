package me.nathanfallet.bdeensisa.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.statuspages.StatusPages
import me.nathanfallet.bdeensisa.public.public
import me.nathanfallet.bdeensisa.account.account
import me.nathanfallet.bdeensisa.admin.admin
import me.nathanfallet.bdeensisa.api.api

fun Application.configureRouting() {
    routing {
        // Register routes
        public()
        account()
        admin()
        api()

        // Static files
        static { resources("static") }
    }
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
            call.response.status(status)
            call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
        }
    }
}
