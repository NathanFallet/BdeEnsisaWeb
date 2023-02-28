package me.nathanfallet.bdeensisa.database

import java.util.Timer
import kotlinx.coroutines.*
import kotlinx.datetime.*
import kotlin.concurrent.scheduleAtFixedRate
import me.nathanfallet.bdeensisa.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*
import io.ktor.server.application.Application

object Database {

    private lateinit var host: String
    private lateinit var name: String
    private lateinit var user: String
    private lateinit var password: String

    fun init(application: Application) {
        // Read configuration
        host = application.environment.config.property("database.host").getString()
        name = application.environment.config.property("database.name").getString()
        user = application.environment.config.property("database.user").getString()
        password = application.environment.config.property("database.password").getString()

        // Connect to database
        val database = org.jetbrains.exposed.sql.Database.connect(
            "jdbc:mysql://$host:3306/$name",
            "com.mysql.cj.jdbc.Driver",
            user, password
        )

        // Create tables (if needed)
        transaction(database) {
            SchemaUtils.create(MenuItems)
            SchemaUtils.create(Users)
            SchemaUtils.create(RegistrationRequests)
            SchemaUtils.create(LoginAuthorizes)
            SchemaUtils.create(PasswordRequests)
            SchemaUtils.create(NotificationsTokens)
            SchemaUtils.create(Cotisants)
            SchemaUtils.create(Permissions)
            SchemaUtils.create(Pages)
            SchemaUtils.create(Topics)
            SchemaUtils.create(Events)
            //SchemaUtils.create(Tickets)
            SchemaUtils.create(Questions)
            SchemaUtils.create(Clubs)
            SchemaUtils.create(ClubMemberships)
        }

        // Launch expiration
        Timer().scheduleAtFixedRate(0, 60 * 60 * 1000L) {
            CoroutineScope(Job()).launch {
                doExpiration()
            }
        }

        // Launch backup
        Timer().scheduleAtFixedRate(0, 24 * 60 * 60 * 1000L) {
            CoroutineScope(Job()).launch {
                doBackup()
            }
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    private suspend fun doExpiration() {
        Database.dbQuery {
            Cotisants.deleteWhere() {
                Op.build { Cotisants.expiration less Clock.System.now().toString() }
            }
            RegistrationRequests.deleteWhere() {
                Op.build { RegistrationRequests.expiration less Clock.System.now().toString() }
            }
            PasswordRequests.deleteWhere() {
                Op.build { PasswordRequests.expiration less Clock.System.now().toString() }
            }
            LoginAuthorizes.deleteWhere() {
                Op.build { LoginAuthorizes.expiration less Clock.System.now().toString() }
            }
            NotificationsTokens.deleteWhere() {
                Op.build { NotificationsTokens.expiration less Clock.System.now().toString() }
            }
            Events.select {
                Events.end less Clock.System.now().minus(1, DateTimeUnit.YEAR, TimeZone.currentSystemDefault()).toString()
            }.forEach {
                Events.delete(it[Events.id])
            }
            Users.select {
                Users.expiration less Clock.System.now().toString()
            }.forEach {
                Users.delete(it[Users.id])
            }
        }
    }

    private suspend fun doBackup() {
        Runtime.getRuntime().exec(
            arrayOf("./backup.sh"),
            arrayOf("DB_HOST=$host", "DB_NAME=$name", "DB_USER=$user", "DB_PASSWORD=$password")
        )
    }

}
