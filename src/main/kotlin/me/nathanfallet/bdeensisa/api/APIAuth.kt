package me.nathanfallet.bdeensisa.api

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.PipelineContext
import java.util.Date
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.*
import org.jetbrains.exposed.sql.*

fun Route.apiAuth() {
    val secret = this.environment!!.config.property("jwt.secret").getString()
    val issuer = this.environment!!.config.property("jwt.issuer").getString()
    val audience = this.environment!!.config.property("jwt.audience").getString()
    val expiration = 365 * 24 * 60 * 60 * 1000L // 1 year

    route("/auth") {
        post {
            // Get code
            val code = try {
                call.receive<UserAuthorize>()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Missing code"))
                return@post
            }

            // Check code and client
            Database.dbQuery {
                LoginAuthorizes.join(Users, JoinType.INNER, LoginAuthorizes.user, Users.id)
                    .select { LoginAuthorizes.code eq code.code }
                    .map { User(it) }
                    .singleOrNull()
            }?.let {
                // Delete code
                Database.dbQuery {
                    LoginAuthorizes.deleteWhere { Op.build { LoginAuthorizes.code eq code.code } }
                }

                // Generate JWT
                val token = JWT.create()
                    .withSubject(it.id)
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withExpiresAt(Date(System.currentTimeMillis() + expiration))
                    .sign(Algorithm.HMAC256(secret))
                call.respond(UserToken(token, it))
            } ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid credentials"))
            }
        }
        authenticate("api-jwt") {
            get {
                getUser()?.let { user ->
                    // Generate a new token
                    val token = JWT.create()
                        .withSubject(user.id)
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .withExpiresAt(Date(System.currentTimeMillis() + expiration))
                        .sign(Algorithm.HMAC256(secret))
                    call.respond(UserToken(token, user))
                } ?: run {
                    call.response.status(HttpStatusCode.Unauthorized)
                    call.respond(mapOf("error" to "Invalid user"))
                }
            }
        }
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.getUser(): User? {
    return call.principal<JWTPrincipal>()?.payload?.getSubject()?.let { userId ->
        Database.dbQuery {
            Users.select { Users.id eq userId }.map { User(it) }.singleOrNull()
        }
    }
}
