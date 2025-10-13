package infrastructure.di

import application.services.JWTService
import application.services.PasswordService
import application.usecase.AddReviewUseCase
import application.usecase.CreateConsultationUseCase
import application.usecase.CreatePatientUseCase
import application.usecase.DoctorUseCase.LoginDoctorUseCase
import application.usecase.DoctorUseCase.RegisterDoctorUseCase
import application.usecase.GetCatalogsUseCase
import domain.interfaces.ConsultationInterface
import domain.interfaces.MedicalRecordInterface
import infrastructure.repositories.CatalogRepositoryImpl
import infrastructure.repositories.ConsultationAggregateRepositoryImpl
import infrastructure.repositories.ConsultationRepositoryImpl
import infrastructure.repositories.DoctorRepositoryImpl
import infrastructure.repositories.PatientAggregateRepositoryImpl
import infrastructure.repositories.ReviewRepositoryImpl

class DependencyContent(


    ) {
    val consultationInterface = ConsultationRepositoryImpl()

    val doctorRepository = DoctorRepositoryImpl()
    val patientRepository = PatientAggregateRepositoryImpl()

    val patientAggregateRepository = PatientAggregateRepositoryImpl()
    val consultationAggregateInterface = ConsultationAggregateRepositoryImpl()
    val consultationAggregateRepository = ConsultationAggregateRepositoryImpl()
    val reviewRepository = ReviewRepositoryImpl()
    val catalogRepository = CatalogRepositoryImpl()

    val passwordService = PasswordService()
    val jwtService = JWTService()

    val registerDoctorUseCase = RegisterDoctorUseCase(doctorRepository, passwordService)
    val loginDoctorUseCase = LoginDoctorUseCase(doctorRepository, passwordService, jwtService)
    val createPatientUseCase = CreatePatientUseCase(patientAggregateRepository)
    val createConsultationUseCase = CreateConsultationUseCase(
        consultationAggregateInterface,
    )
    val addReviewUseCase = AddReviewUseCase(reviewRepository, consultationInterface )
    val getCatalogsUseCase = GetCatalogsUseCase(catalogRepository)
}