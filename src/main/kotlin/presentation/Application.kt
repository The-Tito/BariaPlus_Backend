package com.AntonioSelvas.presentation

import app.AppModule
import application.services.PasswordService
import application.usecase.RegisterDoctorUseCase
import com.AntonioSelvas.configureMonitoring
import com.AntonioSelvas.configureRouting
import com.AntonioSelvas.configureSecurity
import com.AntonioSelvas.configureSerialization
import infrastructure.database.DatabaseConfig
import infrastructure.database.DatabaseFactory
import infrastructure.repositories.DoctorRepositoryImpl
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import presentation.routes.authRoutes.authRoutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    DatabaseFactory.init(DatabaseConfig())

    routing {
        // Ruta de health check
        get("/health") {
            call.respondText("OK", status = io.ktor.http.HttpStatusCode.OK)
        }

        // Rutas de autenticación
        authRoutes(AppModule.registerDoctorUseCase, AppModule.loginDoctorUseCase)
    }

    configureSecurity()
    configureSerialization()
    configureMonitoring()
    configureRouting()
}
