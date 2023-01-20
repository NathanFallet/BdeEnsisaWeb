package me.nathanfallet.bdeensisa.database

import kotlinx.coroutines.*
import me.nathanfallet.bdeensisa.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*
import io.ktor.server.application.Application

object Database {

    fun init(application: Application) {
        val host = application.environment.config.property("database.host").getString()
        val name = application.environment.config.property("database.name").getString()
        val user = application.environment.config.property("database.user").getString()
        val password = application.environment.config.property("database.password").getString()
        val database = org.jetbrains.exposed.sql.Database.connect(
            "jdbc:mysql://$host:3306/$name",
            "com.mysql.cj.jdbc.Driver",
            user, password
        )
        transaction(database) {
            SchemaUtils.create(Users)
            SchemaUtils.create(Cotisants)
            SchemaUtils.create(Pages)
            SchemaUtils.create(Topics)
            SchemaUtils.create(Events)
            SchemaUtils.create(Tickets)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

}
