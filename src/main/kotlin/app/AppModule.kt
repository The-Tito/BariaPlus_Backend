package app

import application.services.JWTService
import application.services.PasswordService
import application.usecase.CreatePatientUseCase
import application.usecase.LoginDoctorUseCase
import application.usecase.RegisterDoctorUseCase
import infrastructure.repositories.DoctorRepositoryImpl
import infrastructure.repositories.PatientAggregateRepositoryImpl
import io.ktor.server.routing.Route
import presentation.routes.authRoutes.*

object AppModule {

    private val doctorRepository by lazy { DoctorRepositoryImpl() }
    private val passwordService by lazy { PasswordService() }
    private val patientAggregateRepository = PatientAggregateRepositoryImpl()
    val jwtService by lazy { JWTService() }

    val registerDoctorUseCase by lazy { RegisterDoctorUseCase(doctorRepository, passwordService) }
    val loginDoctorUseCase by lazy { LoginDoctorUseCase(doctorRepository, passwordService, jwtService) }
    val createPatientUseCase by lazy { CreatePatientUseCase(patientAggregateRepository) }

    val doctorRepositoryForRoutes by lazy { doctorRepository }
}