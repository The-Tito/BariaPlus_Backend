package infrastructure.di

import application.services.CalculationEnergicService
import application.services.CalculationService
import application.services.JWTService
import application.services.PasswordService
import application.services.RangeComparisonService
import application.usecase.AddReviewUseCase
import application.usecase.CreateConsultationUseCase
import application.usecase.PatientUseCase
import application.usecase.DoctorUseCase.LoginDoctorUseCase
import application.usecase.DoctorUseCase.RegisterDoctorUseCase
import application.usecase.GetPatientsFilteredUseCase
import infrastructure.repositories.ConsultationAggregateRepositoryImpl
import infrastructure.repositories.ConsultationRepositoryImpl
import infrastructure.repositories.DoctorRepositoryImpl
import infrastructure.repositories.MedicalRecordRepositoryImpl
import infrastructure.repositories.PatientAggregateRepositoryImpl
import infrastructure.repositories.PhysicalActivityLevelRepository
import infrastructure.repositories.RangeDetailRepository
import infrastructure.repositories.ReviewRepositoryImpl

class DependencyContent(


    ) {
    val rangeDetailRepository = RangeDetailRepository()
    val rangeComparationService = RangeComparisonService(rangeDetailRepository)
    val consultationInterface = ConsultationRepositoryImpl()

    val doctorRepository = DoctorRepositoryImpl()
    val patientRepository = PatientAggregateRepositoryImpl()

    val patientAggregateRepository = PatientAggregateRepositoryImpl()
    val consultationAggregateInterface = ConsultationAggregateRepositoryImpl(rangeComparationService)
    val consultationAggregateRepository = ConsultationAggregateRepositoryImpl(rangeComparationService)
    val reviewRepository = ReviewRepositoryImpl()
    val medicalRepository = MedicalRecordRepositoryImpl()
    val physicalActivityLevelRepository = PhysicalActivityLevelRepository()

    val passwordService = PasswordService()
    val jwtService = JWTService()
    val calculationService = CalculationService()
    val calculationEnergicService = CalculationEnergicService(consultationAggregateRepository)

    val registerDoctorUseCase = RegisterDoctorUseCase(doctorRepository, passwordService)
    val loginDoctorUseCase = LoginDoctorUseCase(doctorRepository, passwordService, jwtService)
    val patientUseCase = PatientUseCase(patientAggregateRepository)
    val createConsultationUseCase = CreateConsultationUseCase(
        consultationAggregateInterface,
        patientRepository,
        medicalRepository,
        calculationService,
        physicalActivityLevelRepository,
        calculationEnergicService,
        rangeComparationService
    )
    val addReviewUseCase = AddReviewUseCase(reviewRepository, consultationInterface )
    val getPatientsFilteredUseCase = GetPatientsFilteredUseCase(patientRepository)
//    val getCatalogsUseCase = GetCatalogsUseCase(catalogRepository)
}