package me.nathanfallet.bdeensisa.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import me.nathanfallet.bdeensisa.account.AccountSession

fun Application.configureSessions() {
    install(Sessions) {
        cookie<AccountSession>("account_session", SessionStorageMemory()) {
            cookie.path = "/"
        }
    }
}