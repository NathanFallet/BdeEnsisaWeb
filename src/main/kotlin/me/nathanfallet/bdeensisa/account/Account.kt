package me.nathanfallet.bdeensisa.account

import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.PipelineContext
import me.nathanfallet.bdeensisa.models.*
import me.nathanfallet.bdeensisa.database.Database
import org.jetbrains.exposed.sql.*

data class AccountSession(val userId: String)

fun Route.account() {
    route("/account") {
        accountLogin()
        accountRegister()
        accountAuthorize()
        accountQRCode()
        accountProfile()
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.getUser(): User? {
    return call.sessions.get<AccountSession>()?.let { session ->
        Database.dbQuery {
            Users.customJoin().select { Users.id eq session.userId }.mapUser(true).singleOrNull()
        }
    }
}
