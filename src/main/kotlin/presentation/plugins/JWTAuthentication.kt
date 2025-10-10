package com.AntonioSelvas.plugins.plugins

import app.AppModule.jwtService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.application.Application
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import kotlinx.serialization.Serializable

fun Application.configureJWTAuthentication() {

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(jwtService.createVerifier())

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

//    install(Authentication) {
//        jwt ("jwt-auth"){
//            realm = config.realm
//            val jwtVerifier = JWT
//                .require(Algorithm.HMAC256(config.secret))
//                .withAudience(config.audience)
//                .withIssuer(config.issuer)
//                .build()
//
//            verifier(jwtVerifier)
//
//            validate { credential ->
//                val username = credential.payload.getClaim("username").asString()
//                if (username.isNullOrBlank()) {
//                    JWTPrincipal(credential.payload)
//                }else{
//                    null
//                }
//            }
//
//            challenge { _, _ ->
//                call.respondText("Token es no valido o ha expirado", status = HttpStatusCode.Unauthorized) }
//
//        }
//    }
//
//data class JWTConfig(
//    val realm: String,
//    val secret: String,
//    val issuer: String,
//    val audience: String,
//    val tokenExpiry: Long,
//)