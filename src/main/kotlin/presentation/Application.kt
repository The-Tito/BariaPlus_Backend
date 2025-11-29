package com.AntonioSelvas.presentation

import com.AntonioSelvas.configureMonitoring
import com.AntonioSelvas.configureRouting
import com.AntonioSelvas.configureSerialization
import com.AntonioSelvas.plugins.plugins.configureJWTAuthentication
import infrastructure.database.DatabaseConfig
import infrastructure.database.DatabaseFactory
import infrastructure.di.DependencyContent
import io.ktor.server.application.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import presentation.routes.authRoutes.authRoutes
import presentation.routes.consultationRoutes
import presentation.routes.doctorRoutes
import presentation.routes.patientRoutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    DatabaseFactory.init(DatabaseConfig())


    val content = DependencyContent()


    configureJWTAuthentication(content)


    routing {
        // Ruta de health check
        get("/health") {
            call.respondText("OK", status = io.ktor.http.HttpStatusCode.OK)
        }


        // Rutas de autenticación (públicas)
        authRoutes(content.registerDoctorUseCase, content.loginDoctorUseCase)


        // Rutas de pacientes (protegidas)
        patientRoutes(content.patientUseCase, content.getPatientsFilteredUseCase)

        // Rutas de consultas (protegidas)
        consultationRoutes(
            content.createConsultationUseCase,
            content.addReviewUseCase,
            content.consultationAggregateRepository,
            content.calculationEnergicService
        )
        doctorRoutes(content.doctorUseCase)
    }
    configureSerialization()
    configureMonitoring()
    configureRouting()
}


