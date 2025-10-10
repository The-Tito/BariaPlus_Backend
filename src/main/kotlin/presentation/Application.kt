package com.AntonioSelvas.presentation

import app.AppModule
import app.AppModule.createPatientUseCase
import app.AppModule.jwtService
import app.AppModule.loginDoctorUseCase
import app.AppModule.registerDoctorUseCase
import application.services.PasswordService
import application.usecase.RegisterDoctorUseCase
import com.AntonioSelvas.configureMonitoring
import com.AntonioSelvas.configureRouting
import com.AntonioSelvas.configureSecurity
import com.AntonioSelvas.configureSerialization
import com.AntonioSelvas.plugins.plugins.configureJWTAuthentication
import infrastructure.database.DatabaseConfig
import infrastructure.database.DatabaseFactory
import infrastructure.repositories.DoctorRepositoryImpl
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import presentation.routes.authRoutes.authRoutes
import presentation.routes.patientRoutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    DatabaseFactory.init(DatabaseConfig())

    configureJWTAuthentication()


    routing {
        // Ruta de health check
        get("/health") {
            call.respondText("OK", status = io.ktor.http.HttpStatusCode.OK)
        }

        // Rutas de autenticación
        authRoutes(registerDoctorUseCase, loginDoctorUseCase)
        patientRoutes(createPatientUseCase)

    }

//    configureSecurity()
    configureSerialization()
    configureMonitoring()
    configureRouting()
}
