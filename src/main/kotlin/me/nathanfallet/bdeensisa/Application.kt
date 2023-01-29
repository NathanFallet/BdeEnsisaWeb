package me.nathanfallet.bdeensisa

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.plugins.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    Database.init(this)
    configureTemplating()
    configureSerialization()
    configureSecurity()
    configureSessions()
    configureRouting()
    configureEmails()
}
