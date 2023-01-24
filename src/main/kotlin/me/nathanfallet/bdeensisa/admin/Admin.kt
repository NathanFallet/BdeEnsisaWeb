package me.nathanfallet.bdeensisa.admin

import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Route.admin() {
    route("/admin") {
        adminDashboard()
        adminMenu()
        adminPages()
        adminUsers()
        adminTopics()
        adminQuestions()
    }
}
