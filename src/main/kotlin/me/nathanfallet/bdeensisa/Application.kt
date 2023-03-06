package me.nathanfallet.bdeensisa

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.plugins.*
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    // Initialize database
    Database.init(this)

    // Initialize plugins
    configureTemplating()
    configureSerialization()
    configureSecurity()
    configureSessions()
    configureRouting()
    configureEmails()

    // Create uploads folder
    val uploadsFolder = Paths.get("uploads")
    if (!Files.exists(uploadsFolder)) {
        Files.createDirectory(uploadsFolder)
    }
}
