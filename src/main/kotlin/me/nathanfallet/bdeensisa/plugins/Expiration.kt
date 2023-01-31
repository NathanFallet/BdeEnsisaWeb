package me.nathanfallet.bdeensisa.plugins

import io.ktor.server.application.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.*
import org.jetbrains.exposed.sql.*

object Expiration {

    fun launchExpiration() {
        // Schedule a task every hour
        Timer().scheduleAtFixedRate(0, 60 * 60 * 1000L) {
            CoroutineScope(Job()).launch {
                doWork()
            }
        }
    }

    private suspend fun doWork() {
        Database.dbQuery {
            Cotisants.deleteWhere() {
                Op.build { Cotisants.expiration less Clock.System.now().toString() }
            }
            RegistrationRequests.deleteWhere() {
                Op.build { RegistrationRequests.expiration less Clock.System.now().toString() }
            }
            LoginAuthorizes.deleteWhere() {
                Op.build { LoginAuthorizes.expiration less Clock.System.now().toString() }
            }
        }
    }

}

fun Application.configureExpiration() {
    Expiration.launchExpiration()
}
