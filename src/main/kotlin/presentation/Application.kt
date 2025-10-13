package com.AntonioSelvas.presentation

import app.AppModule.createPatientUseCase
import app.AppModule.doctorUseCase
import app.AppModule.loginDoctorUseCase
import app.AppModule.registerDoctorUseCase
import com.AntonioSelvas.configureMonitoring
import com.AntonioSelvas.configureRouting
import com.AntonioSelvas.configureSerialization
import com.AntonioSelvas.plugins.plugins.configureJWTAuthentication
import infrastructure.database.DatabaseConfig
import infrastructure.database.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import presentation.routes.authRoutes.authRoutes
import presentation.routes.doctorRoutes
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
        doctorRoutes(doctorUseCase)
        patientRoutes(createPatientUseCase)

    }

//    configureSecurity()
    configureSerialization()
    configureMonitoring()
    configureRouting()
}
