package me.nathanfallet.bdeensisa.account

import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*

data class AccountSession(val userId: String)

fun Route.account() {
    route("/account") {
        login()
        register()
    }
}
