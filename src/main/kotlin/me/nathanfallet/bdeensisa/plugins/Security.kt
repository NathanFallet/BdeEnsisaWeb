package me.nathanfallet.bdeensisa.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*

fun Application.configureSecurity() {
    authentication {
        jwt("api-v1-jwt") {
            val secret =
                    this@configureSecurity.environment.config.property("jwt.secret").getString()
            val issuer =
                    this@configureSecurity.environment.config.property("jwt.issuer").getString()
            val audience =
                    this@configureSecurity.environment.config.property("jwt.audience").getString()
            verifier(
                    JWT.require(Algorithm.HMAC256(secret))
                            .withAudience(audience)
                            .withIssuer(issuer)
                            .build()
            )
            validate {
                JWTPrincipal(it.payload)
            }
            challenge { _, _ ->
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid token"))
            }
        }
    }
}
