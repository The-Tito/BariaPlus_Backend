package app

import application.services.JWTService
import application.services.PasswordService
import application.usecase.LoginDoctorUseCase
import application.usecase.RegisterDoctorUseCase
import infrastructure.repositories.DoctorRepositoryImpl
import io.ktor.server.routing.Route
import presentation.routes.authRoutes.*

object AppModule {

    private val doctorRepository by lazy { DoctorRepositoryImpl() }
    private val passwordService by lazy { PasswordService() }
    val jwtService by lazy { JWTService() }

    val registerDoctorUseCase by lazy { RegisterDoctorUseCase(doctorRepository, passwordService) }
    val loginDoctorUseCase by lazy { LoginDoctorUseCase(doctorRepository, passwordService, jwtService) }

    val doctorRepositoryForRoutes by lazy { doctorRepository }
}