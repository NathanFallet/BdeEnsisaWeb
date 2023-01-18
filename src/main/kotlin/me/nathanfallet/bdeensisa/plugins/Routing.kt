package me.nathanfallet.bdeensisa.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
        get("/") { call.respond(FreeMarkerContent("index.ftl", null)) }
        static { resources("static") }
    }
}
