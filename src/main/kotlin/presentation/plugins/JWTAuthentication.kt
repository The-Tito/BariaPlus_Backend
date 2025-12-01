package com.AntonioSelvas.plugins.plugins


import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import infrastructure.di.DependencyContent
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.application.Application
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import kotlinx.serialization.Serializable

fun Application.configureJWTAuthentication(content: DependencyContent) {



    install(Authentication) {
        jwt("auth-jwt") {
            verifier(content.jwtService.createVerifier())

            validate { credential ->
                // Validar que el token tenga el claim doctorId
                if (credential.payload.getClaim("doctorId").asInt() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse(false, "Token inválido o expirado"))

            }
        }
    }
}

@Serializable
data class ErrorResponse(
    val success: Boolean,
    val message: String
)

