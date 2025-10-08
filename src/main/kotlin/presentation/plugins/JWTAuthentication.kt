package com.AntonioSelvas.plugins.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.application.Application
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respondText

fun Application.configureJWTAuthentication(config: JWTConfig) {
    install(Authentication) {
        jwt ("jwt-auth"){
            realm = config.realm
            val jwtVerifier = JWT
                .require(Algorithm.HMAC256(config.secret))
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .build()

            verifier(jwtVerifier)

            validate { credential ->
                val username = credential.payload.getClaim("username").asString()
                if (username.isNullOrBlank()) {
                    JWTPrincipal(credential.payload)
                }else{
                    null
                }
            }

            challenge { _, _ ->
                call.respondText("Token es no valido o ha expirado", status = HttpStatusCode.Unauthorized) }

        }
    }
}

data class JWTConfig(
    val realm: String,
    val secret: String,
    val issuer: String,
    val audience: String,
    val tokenExpiry: Long,
)